package com.basketman.showtime.team.service

import com.basketman.showtime.attendance.service.AttendanceService
import com.basketman.showtime.meeting.repository.MeetingRepository
import com.basketman.showtime.member.repository.MemberRepository
import com.basketman.showtime.team.dto.MemberProfile
import com.basketman.showtime.team.dto.TeamFromAttendanceRequest
import com.basketman.showtime.team.dto.TeamGenerateRequest
import com.basketman.showtime.team.dto.TeamGenerateResponse
import com.basketman.showtime.team.entity.MeetingTeamAssignmentEntity
import com.basketman.showtime.team.repository.MeetingTeamAssignmentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamGenerationService(
    private val attendanceService: AttendanceService,
    private val meetingRepository: MeetingRepository,
    private val memberRepository: MemberRepository,
    private val meetingTeamAssignmentRepository: MeetingTeamAssignmentRepository,
    private val teamBalancerService: TeamBalancerService,
) {
    @Transactional
    fun generateFromAttendance(request: TeamFromAttendanceRequest): TeamGenerateResponse {
        val meeting = request.meetingId?.let { id ->
            meetingRepository.findById(id).orElseThrow { IllegalArgumentException("meeting not found: $id") }
        }
        if (meeting != null && meeting.meetingDate != request.attendanceDate) {
            throw IllegalArgumentException("attendanceDate must match meetingDate")
        }

        val memberIds = attendanceService.getAttendeeMemberIds(request.attendanceDate)
        if (memberIds.isEmpty()) {
            throw IllegalArgumentException("no attendees found for date ${request.attendanceDate}")
        }

        val membersById = memberRepository.findAllById(memberIds).associateBy { it.id }
        val attendees = memberIds.map { id ->
            val member = membersById[id] ?: throw IllegalArgumentException("member not found: $id")
            if (!member.active) {
                throw IllegalArgumentException("inactive member cannot be assigned: $id")
            }

            MemberProfile(
                id = member.id.toString(),
                name = member.name,
                heightCm = member.heightCm,
                skillLevel = member.skillLevel,
                position = member.position,
                style = member.style,
            )
        }

        val previousTeamByMemberId = meeting?.let { currentMeeting ->
            val previousMeeting = currentMeeting.schedule?.id?.let { scheduleId ->
                meetingRepository.findFirstByScheduleIdAndMeetingDateBeforeOrderByMeetingDateDescStartTimeDesc(
                    scheduleId = scheduleId,
                    meetingDate = currentMeeting.meetingDate,
                )
            } ?: meetingRepository.findFirstByClubIdAndMeetingDateBeforeOrderByMeetingDateDescStartTimeDesc(
                clubId = currentMeeting.club.id,
                meetingDate = currentMeeting.meetingDate,
            )

            previousMeeting?.let { prev ->
                meetingTeamAssignmentRepository.findAllByMeetingId(prev.id)
                    .associate { it.member.id.toString() to it.teamName }
            } ?: emptyMap()
        } ?: emptyMap()

        val result = teamBalancerService.generate(
            TeamGenerateRequest(
                teamNames = request.teamNames,
                attendees = attendees,
                previousTeamByMemberId = previousTeamByMemberId,
            ),
        )

        if (meeting != null) {
            val memberById = membersById.mapKeys { it.key.toString() }
            meetingTeamAssignmentRepository.deleteAllByMeetingId(meeting.id)
            val assignments = result.teams.flatMap { team ->
                team.members.map { member ->
                    MeetingTeamAssignmentEntity(
                        meeting = meeting,
                        member = memberById[member.id]
                            ?: throw IllegalStateException("member not found while persisting assignment: ${member.id}"),
                        teamName = team.name,
                    )
                }
            }
            meetingTeamAssignmentRepository.saveAll(assignments)
        }

        return result
    }
}
