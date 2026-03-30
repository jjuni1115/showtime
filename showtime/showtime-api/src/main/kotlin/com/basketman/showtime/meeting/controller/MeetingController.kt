package com.basketman.showtime.meeting.controller

import com.basketman.showtime.auth.CurrentUserProvider
import com.basketman.showtime.meeting.dto.CreateMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.CreateMeetingRequest
import com.basketman.showtime.meeting.dto.MeetingResponse
import com.basketman.showtime.meeting.dto.MeetingScheduleResponse
import com.basketman.showtime.meeting.dto.SetGuestAttendanceRequest
import com.basketman.showtime.meeting.dto.SetMemberAttendanceRequest
import com.basketman.showtime.meeting.dto.UpdateMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.UpdateMeetingRequest
import com.basketman.showtime.meeting.dto.VoteAttendanceRequest
import com.basketman.showtime.meeting.service.MeetingService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class MeetingController(
    private val currentUserProvider: CurrentUserProvider,
    private val meetingService: MeetingService,
) {
    @PostMapping("/clubs/{clubId}/meeting-schedules")
    fun createSchedule(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @Valid @RequestBody request: CreateMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.createSchedule(clubId, currentUser, request)
    }

    @GetMapping("/clubs/{clubId}/meeting-schedules")
    fun getSchedules(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
    ): List<MeetingScheduleResponse> {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.getSchedules(clubId, currentUser)
    }

    @PutMapping("/clubs/{clubId}/meeting-schedules/{scheduleId}")
    fun updateSchedule(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @PathVariable scheduleId: Long,
        @Valid @RequestBody request: UpdateMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.updateSchedule(clubId, scheduleId, currentUser, request)
    }

    @DeleteMapping("/clubs/{clubId}/meeting-schedules/{scheduleId}")
    fun deleteSchedule(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @PathVariable scheduleId: Long,
    ) {
        val currentUser = currentUserProvider.resolve(principal)
        meetingService.deleteSchedule(clubId, scheduleId, currentUser)
    }

    @PostMapping("/clubs/{clubId}/meetings")
    fun createMeeting(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @Valid @RequestBody request: CreateMeetingRequest,
    ): MeetingResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.createMeeting(clubId, currentUser, request)
    }

    @GetMapping("/clubs/{clubId}/meetings")
    fun getMeetings(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
    ): List<MeetingResponse> {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.getMeetings(clubId, currentUser, from, to)
    }

    @PutMapping("/meetings/{meetingId}")
    fun updateMeeting(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable meetingId: UUID,
        @Valid @RequestBody request: UpdateMeetingRequest,
    ): MeetingResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.updateMeeting(meetingId, currentUser, request)
    }

    @DeleteMapping("/meetings/{meetingId}")
    fun deleteMeeting(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable meetingId: UUID,
    ) {
        val currentUser = currentUserProvider.resolve(principal)
        meetingService.deleteMeeting(meetingId, currentUser)
    }

    @PostMapping("/meetings/{meetingId}/attendance/me")
    fun voteMyAttendance(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable meetingId: UUID,
        @Valid @RequestBody request: VoteAttendanceRequest,
    ): MeetingResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.voteMyAttendance(meetingId, currentUser, request)
    }

    @PostMapping("/meetings/{meetingId}/attendance/guest")
    fun setGuestAttendance(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable meetingId: UUID,
        @Valid @RequestBody request: SetGuestAttendanceRequest,
    ): MeetingResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.setGuestAttendance(meetingId, currentUser, request)
    }

    @PostMapping("/meetings/{meetingId}/attendance/member")
    fun setMemberAttendance(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable meetingId: UUID,
        @Valid @RequestBody request: SetMemberAttendanceRequest,
    ): MeetingResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.setMemberAttendance(meetingId, currentUser, request)
    }
}
