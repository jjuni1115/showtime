package com.basketman.showtime.match.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.Instant
import java.time.LocalDate

data class CreateMatchResultRequest(
    @field:NotNull
    val playedAt: LocalDate,
    @field:NotEmpty
    val teamScores: Map<@NotBlank String, @PositiveOrZero Int>,
    val memo: String? = null,
)

data class MatchResultResponse(
    val id: String,
    val playedAt: LocalDate,
    val teamScores: Map<String, Int>,
    val memo: String?,
    val videos: List<VideoMetadataResponse>,
    val createdAt: Instant,
)

data class VideoMetadataResponse(
    val id: String,
    val fileName: String,
    val contentType: String,
    val sizeBytes: Long,
    val uploadedAt: Instant,
    val downloadUrl: String,
)
