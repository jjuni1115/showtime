package com.basketman.showtime.meeting.dto

import com.basketman.showtime.meeting.entity.AttendanceStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class UpsertMeetingScheduleRequest(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val enabled: Boolean = true,
)

data class MeetingScheduleResponse(
    val clubId: UUID,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val enabled: Boolean,
)

data class CreateMeetingRequest(
    @field:NotNull
    val meetingDate: LocalDate,
    val startTime: LocalTime? = null,
    val note: String? = null,
)

data class MeetingAttendanceResponse(
    val userId: UUID?,
    val userName: String?,
    val guestName: String?,
    val status: AttendanceStatus,
    val source: String,
)

data class MeetingResponse(
    val id: UUID,
    val clubId: UUID,
    val meetingDate: LocalDate,
    val startTime: LocalTime,
    val note: String?,
    val attendances: List<MeetingAttendanceResponse>,
)

data class VoteAttendanceRequest(
    val status: AttendanceStatus,
)

data class SetGuestAttendanceRequest(
    @field:NotBlank
    val guestName: String,
    val status: AttendanceStatus,
)

data class SetMemberAttendanceRequest(
    @field:NotNull
    val userId: UUID,
    val status: AttendanceStatus,
)
