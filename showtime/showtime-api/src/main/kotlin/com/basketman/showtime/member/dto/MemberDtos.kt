package com.basketman.showtime.member.dto

import com.basketman.showtime.team.PlayStyle
import com.basketman.showtime.team.Position
import com.basketman.showtime.team.SkillLevel
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class CreateMemberRequest(
    @field:NotBlank
    val name: String,
    @field:Min(130)
    @field:Max(240)
    val heightCm: Int,
    val skillLevel: SkillLevel,
    val position: Position,
    val style: PlayStyle,
)

data class MemberResponse(
    val id: UUID,
    val name: String,
    val heightCm: Int,
    val skillLevel: SkillLevel,
    val position: Position,
    val style: PlayStyle,
    val active: Boolean,
)
