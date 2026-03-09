package com.basketman.showtime.team.dto

import com.basketman.showtime.team.PlayStyle
import com.basketman.showtime.team.Position
import com.basketman.showtime.team.SkillLevel
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class TeamGenerateRequest(
    @field:NotEmpty
    val teamNames: List<@NotBlank String>,
    @field:NotEmpty
    val attendees: List<@Valid MemberProfile>,
    val previousTeamByMemberId: Map<String, String> = emptyMap(),
)

data class MemberProfile(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val name: String,
    @field:Min(130)
    @field:Max(240)
    val heightCm: Int,
    val skillLevel: SkillLevel,
    val position: Position,
    val style: PlayStyle,
)

data class TeamGenerateResponse(
    val teams: List<TeamResult>,
    val summary: BalanceSummary,
)

data class TeamResult(
    val name: String,
    val members: List<MemberProfile>,
    val totalSkillScore: Int,
    val averageHeightCm: Double,
    val positionCounts: Map<Position, Int>,
    val repeatedFromLastWeekCount: Int,
)

data class BalanceSummary(
    val teamSkillGap: Int,
    val teamSizeGap: Int,
    val repeatedAssignments: Int,
)
