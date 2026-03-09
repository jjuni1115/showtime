package com.basketman.showtime.community.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class CreatePostRequest(
    @field:NotBlank
    @field:Size(max = 2000)
    val content: String,
)

data class PostResponse(
    val id: UUID,
    val clubId: UUID,
    val authorUserId: UUID,
    val authorName: String,
    val content: String,
    val createdAt: Instant,
)
