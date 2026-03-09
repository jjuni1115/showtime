package com.basketman.showtime.team.service

import com.basketman.showtime.team.Position
import com.basketman.showtime.team.dto.BalanceSummary
import com.basketman.showtime.team.dto.MemberProfile
import com.basketman.showtime.team.dto.TeamGenerateRequest
import com.basketman.showtime.team.dto.TeamGenerateResponse
import com.basketman.showtime.team.dto.TeamResult
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class TeamBalancerService {
    fun generate(request: TeamGenerateRequest): TeamGenerateResponse {
        require(request.teamNames.isNotEmpty()) { "teamNames must not be empty" }
        require(request.attendees.size >= request.teamNames.size) {
            "attendees must be greater than or equal to team count"
        }

        val teamStates = request.teamNames.map { MutableTeamState(it) }
        val sortedMembers = request.attendees.sortedByDescending { it.skillLevel.score }
        val targetSizes = calculateTargetSizes(request.attendees.size, teamStates.size)
        val targetSkillPerTeam = request.attendees.sumOf { it.skillLevel.score }.toDouble() / teamStates.size
        val overallPositionCounts = request.attendees.groupingBy { it.position }.eachCount()

        sortedMembers.forEach { member ->
            val chosen = teamStates.minBy { state ->
                placementPenalty(
                    member = member,
                    state = state,
                    allStates = teamStates,
                    targetSizes = targetSizes,
                    previousTeamByMemberId = request.previousTeamByMemberId,
                    targetSkillPerTeam = targetSkillPerTeam,
                    overallPositionCounts = overallPositionCounts,
                )
            }
            chosen.members.add(member)
        }

        val teamResults = teamStates.map { state ->
            val repeatedCount = state.members.count { member ->
                request.previousTeamByMemberId[member.id] == state.name
            }

            TeamResult(
                name = state.name,
                members = state.members.toList(),
                totalSkillScore = state.totalSkill(),
                averageHeightCm = state.averageHeight(),
                positionCounts = state.members.groupingBy { it.position }.eachCount(),
                repeatedFromLastWeekCount = repeatedCount,
            )
        }

        val skillTotals = teamResults.map { it.totalSkillScore }
        val summary = BalanceSummary(
            teamSkillGap = skillTotals.max() - skillTotals.min(),
            teamSizeGap = teamResults.maxOf { it.members.size } - teamResults.minOf { it.members.size },
            repeatedAssignments = teamResults.sumOf { it.repeatedFromLastWeekCount },
        )

        return TeamGenerateResponse(teams = teamResults, summary = summary)
    }

    private fun placementPenalty(
        member: MemberProfile,
        state: MutableTeamState,
        allStates: List<MutableTeamState>,
        targetSizes: List<Int>,
        previousTeamByMemberId: Map<String, String>,
        targetSkillPerTeam: Double,
        overallPositionCounts: Map<Position, Int>,
    ): Double {
        val teamIndex = allStates.indexOf(state)
        val targetSize = targetSizes[teamIndex]
        val projectedSize = state.members.size + 1
        if (projectedSize > targetSize) {
            return Double.MAX_VALUE / 8
        }

        val projectedSkill = state.totalSkill() + member.skillLevel.score
        val skillPenalty = abs(projectedSkill - targetSkillPerTeam) * 1.9

        val currentPositionCount = state.members.count { it.position == member.position }
        val projectedPositionCount = currentPositionCount + 1
        val desiredPositionPerTeam = (overallPositionCounts[member.position] ?: 0).toDouble() / allStates.size
        val positionPenalty = abs(projectedPositionCount - desiredPositionPerTeam) * 2.3

        val currentStyleCount = state.members.count { it.style == member.style }
        val stylePenalty = currentStyleCount * 0.8

        val repeatedPenalty = if (previousTeamByMemberId[member.id] == state.name) 6.0 else 0.0

        val currentHeightAvg = if (state.members.isEmpty()) member.heightCm.toDouble() else state.averageHeight()
        val projectedHeightAvg = (state.members.sumOf { it.heightCm } + member.heightCm).toDouble() / projectedSize
        val heightPenalty = abs(projectedHeightAvg - currentHeightAvg) * 0.03

        return skillPenalty + positionPenalty + stylePenalty + repeatedPenalty + heightPenalty
    }

    private fun calculateTargetSizes(attendeeCount: Int, teamCount: Int): List<Int> {
        val baseSize = attendeeCount / teamCount
        val extra = attendeeCount % teamCount
        return List(teamCount) { index -> if (index < extra) baseSize + 1 else baseSize }
    }
}

private data class MutableTeamState(
    val name: String,
    val members: MutableList<MemberProfile> = mutableListOf(),
) {
    fun totalSkill(): Int = members.sumOf { it.skillLevel.score }
    fun averageHeight(): Double = members.map { it.heightCm }.average()
}
