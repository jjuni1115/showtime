package com.basketman.showtime.meeting.repository

import com.basketman.showtime.meeting.entity.MeetingScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeetingScheduleRepository : JpaRepository<MeetingScheduleEntity, Long> {
    fun findByClubId(clubId: UUID): MeetingScheduleEntity?
}
