package com.basketman.showtime.meeting.dto

import com.basketman.showtime.meeting.entity.AttendanceStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class CreateMeetingScheduleRequest(
    @field:NotBlank
    val name: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val place: String? = null,
    val enabled: Boolean = true,
)

data class UpdateMeetingScheduleRequest(
    @field:NotBlank
    val name: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val place: String? = null,
    val enabled: Boolean = true,
)

data class MeetingScheduleResponse(
    val id: Long,
    val clubId: UUID,
    val name: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val place: String?,
    val enabled: Boolean,
)

data class CreateMeetingRequest(
    @field:NotNull
    val scheduleId: Long,
    @field:NotNull
    val meetingDate: LocalDate,
    val note: String? = null,
)

data class UpdateMeetingRequest(
    @field:NotNull
    val scheduleId: Long,
    @field:NotNull
    val meetingDate: LocalDate,
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
    val scheduleId: Long?,
    val scheduleName: String?,
    val meetingDate: LocalDate,
    val startTime: LocalTime,
    val place: String?,
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
