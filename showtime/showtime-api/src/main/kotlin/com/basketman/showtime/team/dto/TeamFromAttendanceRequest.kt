package com.basketman.showtime.team.dto

import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate
import java.util.UUID

data class TeamFromAttendanceRequest(
    val meetingId: UUID? = null,
    val attendanceDate: LocalDate,
    @field:NotEmpty
    val teamNames: List<String>,
)
