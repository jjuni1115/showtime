package com.basketman.showtime.meeting.controller

import com.basketman.showtime.auth.CurrentUserProvider
import com.basketman.showtime.meeting.dto.CreateMeetingRequest
import com.basketman.showtime.meeting.dto.MeetingResponse
import com.basketman.showtime.meeting.dto.MeetingScheduleResponse
import com.basketman.showtime.meeting.dto.SetGuestAttendanceRequest
import com.basketman.showtime.meeting.dto.SetMemberAttendanceRequest
import com.basketman.showtime.meeting.dto.UpsertMeetingScheduleRequest
import com.basketman.showtime.meeting.dto.VoteAttendanceRequest
import com.basketman.showtime.meeting.service.MeetingService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
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
    @PutMapping("/clubs/{clubId}/meeting-schedule")
    fun upsertSchedule(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @Valid @RequestBody request: UpsertMeetingScheduleRequest,
    ): MeetingScheduleResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.upsertSchedule(clubId, currentUser, request)
    }

    @GetMapping("/clubs/{clubId}/meeting-schedule")
    fun getSchedule(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
    ): MeetingScheduleResponse? {
        val currentUser = currentUserProvider.resolve(principal)
        return meetingService.getSchedule(clubId, currentUser)
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
