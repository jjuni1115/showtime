package com.basketman.showtime.meeting.service

import com.basketman.showtime.club.entity.MembershipRole
import com.basketman.showtime.club.repository.ClubMembershipRepository
import com.basketman.showtime.club.repository.ClubRepository
import com.basketman.showtime.club.service.ClubService
import com.basketman.showtime.meeting.dto.CreateMeetingRequest
import com.basketman.showtime.meeting.dto.MeetingAttendanceResponse
import com.basketman.showtime.meeting.dto.MeetingResponse
import com.basketman.showtime.meeting.dto.MeetingScheduleResponse
import com.basketman.showtime.meeting.dto.SetGuestAttendanceRequest
import com.basketman.showtime.meeting.dto.SetMemberAttendanceRequest
import com.basketman.showtime.meeting.dto.UpsertMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.VoteAttendanceRequest
import com.basketman.showtime.meeting.entity.AttendanceSource
import com.basketman.showtime.meeting.entity.MeetingAttendanceEntity
import com.basketman.showtime.meeting.entity.MeetingEntity
import com.basketman.showtime.meeting.entity.MeetingScheduleEntity
import com.basketman.showtime.meeting.repository.MeetingAttendanceRepository
import com.basketman.showtime.meeting.repository.MeetingRepository
import com.basketman.showtime.meeting.repository.MeetingScheduleRepository
import com.basketman.showtime.user.entity.AppUserEntity
import com.basketman.showtime.user.repository.AppUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class MeetingService(
    private val clubService: ClubService,
    private val clubRepository: ClubRepository,
    private val clubMembershipRepository: ClubMembershipRepository,
    private val appUserRepository: AppUserRepository,
    private val meetingScheduleRepository: MeetingScheduleRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingAttendanceRepository: MeetingAttendanceRepository,
) {
    @Transactional
    fun upsertSchedule(
        clubId: UUID,
        currentUser: AppUserEntity,
        request: UpsertMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        clubService.ensureAdmin(clubId, currentUser.id)
        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val existing = meetingScheduleRepository.findByClubId(clubId)
        val saved = if (existing == null) {
            meetingScheduleRepository.save(
                MeetingScheduleEntity(
                    club = club,
                    dayOfWeek = request.dayOfWeek,
                    startTime = request.startTime,
                    enabled = request.enabled,
                    updatedByUser = currentUser,
                    updatedAt = Instant.now(),
                ),
            )
        } else {
            existing.dayOfWeek = request.dayOfWeek
            existing.startTime = request.startTime
            existing.enabled = request.enabled
            existing.updatedByUser = currentUser
            existing.updatedAt = Instant.now()
            meetingScheduleRepository.save(existing)
        }

        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getSchedule(clubId: UUID, currentUser: AppUserEntity): MeetingScheduleResponse? {
        clubService.ensureMember(clubId, currentUser.id)
        return meetingScheduleRepository.findByClubId(clubId)?.toResponse()
    }

    @Transactional
    fun createMeeting(clubId: UUID, currentUser: AppUserEntity, request: CreateMeetingRequest): MeetingResponse {
        clubService.ensureAdmin(clubId, currentUser.id)

        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val schedule = meetingScheduleRepository.findByClubId(clubId)
        val meetingStartTime = request.startTime ?: schedule?.startTime
            ?: throw IllegalArgumentException("startTime is required when no schedule exists")

        val meeting = meetingRepository.save(
            MeetingEntity(
                club = club,
                meetingDate = request.meetingDate,
                startTime = meetingStartTime,
                note = request.note,
                createdByUser = currentUser,
            ),
        )

        return meeting.toResponse()
    }

    @Transactional(readOnly = true)
    fun getMeetings(clubId: UUID, currentUser: AppUserEntity, from: LocalDate, to: LocalDate): List<MeetingResponse> {
        clubService.ensureMember(clubId, currentUser.id)
        return meetingRepository.findAllByClubIdAndMeetingDateBetweenOrderByMeetingDateAsc(clubId, from, to)
            .map { it.toResponse() }
    }

    @Transactional
    fun voteMyAttendance(meetingId: UUID, currentUser: AppUserEntity, request: VoteAttendanceRequest): MeetingResponse {
        val meeting = findMeeting(meetingId)
        clubService.ensureMember(meeting.club.id, currentUser.id)

        val existing = meetingAttendanceRepository.findByMeetingIdAndUserId(meetingId, currentUser.id)
        if (existing == null) {
            meetingAttendanceRepository.save(
                MeetingAttendanceEntity(
                    meeting = meeting,
                    user = currentUser,
                    status = request.status,
                    source = AttendanceSource.MEMBER_VOTE,
                    updatedByUser = currentUser,
                    updatedAt = Instant.now(),
                ),
            )
        } else {
            existing.status = request.status
            existing.source = AttendanceSource.MEMBER_VOTE
            existing.updatedByUser = currentUser
            existing.updatedAt = Instant.now()
            meetingAttendanceRepository.save(existing)
        }

        return findMeeting(meetingId).toResponse()
    }

    @Transactional
    fun setGuestAttendance(meetingId: UUID, currentUser: AppUserEntity, request: SetGuestAttendanceRequest): MeetingResponse {
        val meeting = findMeeting(meetingId)
        clubService.ensureAdmin(meeting.club.id, currentUser.id)

        val guest = request.guestName.trim()
        if (guest.isBlank()) {
            throw IllegalArgumentException("guestName must not be blank")
        }

        val existing = meetingAttendanceRepository.findByMeetingIdAndGuestName(meetingId, guest)
        if (existing == null) {
            meetingAttendanceRepository.save(
                MeetingAttendanceEntity(
                    meeting = meeting,
                    guestName = guest,
                    status = request.status,
                    source = AttendanceSource.ADMIN_SET,
                    updatedByUser = currentUser,
                    updatedAt = Instant.now(),
                ),
            )
        } else {
            existing.status = request.status
            existing.source = AttendanceSource.ADMIN_SET
            existing.updatedByUser = currentUser
            existing.updatedAt = Instant.now()
            meetingAttendanceRepository.save(existing)
        }

        return findMeeting(meetingId).toResponse()
    }

    @Transactional
    fun setMemberAttendance(meetingId: UUID, currentUser: AppUserEntity, request: SetMemberAttendanceRequest): MeetingResponse {
        val meeting = findMeeting(meetingId)
        clubService.ensureAdmin(meeting.club.id, currentUser.id)

        val member = appUserRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("user not found: ${request.userId}") }

        val isMember = clubMembershipRepository.findByClubIdAndUserId(meeting.club.id, member.id) != null
        if (!isMember) {
            throw IllegalArgumentException("target user is not a club member")
        }

        val existing = meetingAttendanceRepository.findByMeetingIdAndUserId(meetingId, member.id)
        if (existing == null) {
            meetingAttendanceRepository.save(
                MeetingAttendanceEntity(
                    meeting = meeting,
                    user = member,
                    status = request.status,
                    source = AttendanceSource.ADMIN_SET,
                    updatedByUser = currentUser,
                    updatedAt = Instant.now(),
                ),
            )
        } else {
            existing.status = request.status
            existing.source = AttendanceSource.ADMIN_SET
            existing.updatedByUser = currentUser
            existing.updatedAt = Instant.now()
            meetingAttendanceRepository.save(existing)
        }

        return findMeeting(meetingId).toResponse()
    }

    private fun findMeeting(meetingId: UUID): MeetingEntity {
        return meetingRepository.findById(meetingId)
            .orElseThrow { IllegalArgumentException("meeting not found: $meetingId") }
    }

    private fun MeetingScheduleEntity.toResponse(): MeetingScheduleResponse {
        return MeetingScheduleResponse(
            clubId = club.id,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            enabled = enabled,
        )
    }

    private fun MeetingEntity.toResponse(): MeetingResponse {
        return MeetingResponse(
            id = id,
            clubId = club.id,
            meetingDate = meetingDate,
            startTime = startTime,
            note = note,
            attendances = attendances.map {
                MeetingAttendanceResponse(
                    userId = it.user?.id,
                    userName = it.user?.displayName,
                    guestName = it.guestName,
                    status = it.status,
                    source = it.source.name,
                )
            },
        )
    }
}
