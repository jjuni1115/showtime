package com.basketman.showtime.meeting.service

import com.basketman.showtime.club.repository.ClubMembershipRepository
import com.basketman.showtime.club.repository.ClubRepository
import com.basketman.showtime.club.service.ClubService
import com.basketman.showtime.meeting.dto.CreateMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.CreateMeetingRequest
import com.basketman.showtime.meeting.dto.MeetingAttendanceResponse
import com.basketman.showtime.meeting.dto.MeetingResponse
import com.basketman.showtime.meeting.dto.MeetingScheduleResponse
import com.basketman.showtime.meeting.dto.SetGuestAttendanceRequest
import com.basketman.showtime.meeting.dto.SetMemberAttendanceRequest
import com.basketman.showtime.meeting.dto.UpdateMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.UpdateMeetingRequest
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
import java.time.DayOfWeek
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
    fun createSchedule(
        clubId: UUID,
        currentUser: AppUserEntity,
        request: CreateMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        clubService.ensureAdmin(clubId, currentUser.id)
        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val saved = meetingScheduleRepository.save(
            MeetingScheduleEntity(
                club = club,
                name = request.name.trim(),
                dayOfWeek = request.dayOfWeek,
                startTime = request.startTime,
                place = request.place?.trim()?.ifBlank { null },
                enabled = request.enabled,
                updatedByUser = currentUser,
                updatedAt = Instant.now(),
            ),
        )
        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getSchedules(clubId: UUID, currentUser: AppUserEntity): List<MeetingScheduleResponse> {
        clubService.ensureMember(clubId, currentUser.id)
        return meetingScheduleRepository.findAllByClubIdOrderByIdAsc(clubId)
            .map { it.toResponse() }
    }

    @Transactional
    fun updateSchedule(
        clubId: UUID,
        scheduleId: Long,
        currentUser: AppUserEntity,
        request: UpdateMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        clubService.ensureAdmin(clubId, currentUser.id)
        val existing = meetingScheduleRepository.findByIdAndClubId(scheduleId, clubId)
            ?: throw IllegalArgumentException("meeting schedule not found: $scheduleId")

        existing.name = request.name.trim()
        existing.dayOfWeek = request.dayOfWeek
        existing.startTime = request.startTime
        existing.place = request.place?.trim()?.ifBlank { null }
        existing.enabled = request.enabled
        existing.updatedByUser = currentUser
        existing.updatedAt = Instant.now()

        return meetingScheduleRepository.save(existing).toResponse()
    }

    @Transactional
    fun deleteSchedule(clubId: UUID, scheduleId: Long, currentUser: AppUserEntity) {
        clubService.ensureAdmin(clubId, currentUser.id)
        val existing = meetingScheduleRepository.findByIdAndClubId(scheduleId, clubId)
            ?: throw IllegalArgumentException("meeting schedule not found: $scheduleId")
        meetingScheduleRepository.delete(existing)
    }

    @Transactional
    fun createMeeting(clubId: UUID, currentUser: AppUserEntity, request: CreateMeetingRequest): MeetingResponse {
        clubService.ensureAdmin(clubId, currentUser.id)

        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val schedule = meetingScheduleRepository.findByIdAndClubId(request.scheduleId, clubId)
            ?: throw IllegalArgumentException("meeting schedule not found: ${request.scheduleId}")
        validateMeetingDateBySchedule(schedule.dayOfWeek, request.meetingDate)

        val meeting = meetingRepository.save(
            MeetingEntity(
                club = club,
                schedule = schedule,
                meetingDate = request.meetingDate,
                startTime = schedule.startTime,
                place = schedule.place,
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
    fun updateMeeting(meetingId: UUID, currentUser: AppUserEntity, request: UpdateMeetingRequest): MeetingResponse {
        val meeting = findMeeting(meetingId)
        clubService.ensureAdmin(meeting.club.id, currentUser.id)
        val schedule = meetingScheduleRepository.findByIdAndClubId(request.scheduleId, meeting.club.id)
            ?: throw IllegalArgumentException("meeting schedule not found: ${request.scheduleId}")
        validateMeetingDateBySchedule(schedule.dayOfWeek, request.meetingDate)

        meeting.schedule = schedule
        meeting.meetingDate = request.meetingDate
        meeting.startTime = schedule.startTime
        meeting.place = schedule.place
        meeting.note = request.note?.trim()?.ifBlank { null }

        val saved = meetingRepository.save(meeting)
        return saved.toResponse()
    }

    @Transactional
    fun deleteMeeting(meetingId: UUID, currentUser: AppUserEntity) {
        val meeting = findMeeting(meetingId)
        clubService.ensureAdmin(meeting.club.id, currentUser.id)
        meetingRepository.delete(meeting)
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
            id = id ?: throw IllegalStateException("meeting schedule id is null"),
            clubId = club.id,
            name = name,
            dayOfWeek = dayOfWeek,
            startTime = startTime,
            place = place,
            enabled = enabled,
        )
    }

    private fun MeetingEntity.toResponse(): MeetingResponse {
        return MeetingResponse(
            id = id,
            clubId = club.id,
            scheduleId = schedule?.id,
            scheduleName = schedule?.name,
            meetingDate = meetingDate,
            startTime = startTime,
            place = place,
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

    private fun validateMeetingDateBySchedule(dayOfWeek: DayOfWeek, meetingDate: LocalDate) {
        if (meetingDate.dayOfWeek != dayOfWeek) {
            throw IllegalArgumentException("meetingDate must match schedule dayOfWeek")
        }

        val today = LocalDate.now()
        val lastAllowed = today.plusDays(31)
        if (meetingDate.isBefore(today) || meetingDate.isAfter(lastAllowed)) {
            throw IllegalArgumentException("meetingDate must be within 1 month from today")
        }
    }
}
