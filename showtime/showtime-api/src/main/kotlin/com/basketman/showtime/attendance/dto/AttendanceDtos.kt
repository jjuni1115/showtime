package com.basketman.showtime.attendance.dto

import com.basketman.showtime.member.dto.MemberResponse
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate
import java.util.UUID

data class UpsertAttendanceRequest(
    @field:NotEmpty
    val memberIds: List<UUID>,
)

data class AttendanceResponse(
    val date: LocalDate,
    val attendees: List<MemberResponse>,
)
