package com.basketman.showtime.meeting.repository

import com.basketman.showtime.meeting.entity.MeetingAttendanceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeetingAttendanceRepository : JpaRepository<MeetingAttendanceEntity, Long> {
    fun findByMeetingIdAndUserId(meetingId: UUID, userId: UUID): MeetingAttendanceEntity?
    fun findByMeetingIdAndGuestName(meetingId: UUID, guestName: String): MeetingAttendanceEntity?
}
