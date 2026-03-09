package com.basketman.showtime.attendance.controller

import com.basketman.showtime.attendance.dto.AttendanceResponse
import com.basketman.showtime.attendance.dto.UpsertAttendanceRequest
import com.basketman.showtime.attendance.service.AttendanceService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/attendances")
class AttendanceController(
    private val attendanceService: AttendanceService,
) {
    @PutMapping("/{date}")
    fun upsert(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @Valid @RequestBody request: UpsertAttendanceRequest,
    ): AttendanceResponse {
        return attendanceService.upsert(date, request)
    }

    @GetMapping("/{date}")
    fun getByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
    ): AttendanceResponse {
        return attendanceService.getByDate(date)
    }
}
