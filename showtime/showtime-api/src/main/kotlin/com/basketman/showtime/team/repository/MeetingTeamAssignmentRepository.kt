package com.basketman.showtime.team.repository

import com.basketman.showtime.team.entity.MeetingTeamAssignmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MeetingTeamAssignmentRepository : JpaRepository<MeetingTeamAssignmentEntity, Long> {
    fun findAllByMeetingId(meetingId: UUID): List<MeetingTeamAssignmentEntity>
    fun deleteAllByMeetingId(meetingId: UUID)
}
