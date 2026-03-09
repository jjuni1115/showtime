package com.basketman.showtime.team.service

import com.basketman.showtime.attendance.service.AttendanceService
import com.basketman.showtime.member.repository.MemberRepository
import com.basketman.showtime.team.dto.MemberProfile
import com.basketman.showtime.team.dto.TeamFromAttendanceRequest
import com.basketman.showtime.team.dto.TeamGenerateRequest
import com.basketman.showtime.team.dto.TeamGenerateResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamGenerationService(
    private val attendanceService: AttendanceService,
    private val memberRepository: MemberRepository,
    private val teamBalancerService: TeamBalancerService,
) {
    @Transactional(readOnly = true)
    fun generateFromAttendance(request: TeamFromAttendanceRequest): TeamGenerateResponse {
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

        return teamBalancerService.generate(
            TeamGenerateRequest(
                teamNames = request.teamNames,
                attendees = attendees,
                previousTeamByMemberId = request.previousTeamByMemberId,
            ),
        )
    }
}
