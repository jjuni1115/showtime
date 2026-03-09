package com.basketman.showtime.attendance.repository

import com.basketman.showtime.attendance.entity.AttendanceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface AttendanceRepository : JpaRepository<AttendanceEntity, Long> {
    fun findAllByAttendanceDate(attendanceDate: LocalDate): List<AttendanceEntity>
    fun deleteAllByAttendanceDate(attendanceDate: LocalDate)
}
