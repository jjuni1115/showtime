package com.basketman.showtime.club.dto

import com.basketman.showtime.club.entity.MembershipRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class CreateClubRequest(
    @field:NotBlank
    @field:Size(max = 120)
    val name: String,
    @field:NotBlank
    @field:Size(max = 255)
    val homeCourt: String,
    @field:Size(max = 1000)
    val imageUrl: String? = null,
)

data class ClubResponse(
    val id: UUID,
    val name: String,
    val homeCourt: String,
    val imageUrl: String?,
    val myRole: MembershipRole,
)

data class InviteResponse(
    val code: String,
    val inviteLink: String,
    val expiresAt: Instant?,
)

data class JoinClubRequest(
    @field:NotBlank
    val code: String,
)
