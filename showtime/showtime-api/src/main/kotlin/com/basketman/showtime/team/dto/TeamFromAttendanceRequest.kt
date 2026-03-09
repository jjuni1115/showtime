package com.basketman.showtime.team.dto

import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate

data class TeamFromAttendanceRequest(
    val attendanceDate: LocalDate,
    @field:NotEmpty
    val teamNames: List<String>,
    val previousTeamByMemberId: Map<String, String> = emptyMap(),
)
