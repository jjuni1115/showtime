package com.basketman.showtime.user.dto

import java.util.UUID

data class MyProfileResponse(
    val id: UUID,
    val email: String,
    val displayName: String,
)
