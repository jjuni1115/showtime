package com.basketman.showtime.meeting.repository

import com.basketman.showtime.meeting.entity.MeetingEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface MeetingRepository : JpaRepository<MeetingEntity, UUID> {
    @EntityGraph(attributePaths = ["schedule", "attendances", "attendances.user"])
    fun findAllByClubIdAndMeetingDateBetweenOrderByMeetingDateAsc(clubId: UUID, from: LocalDate, to: LocalDate): List<MeetingEntity>

    @EntityGraph(attributePaths = ["schedule", "attendances", "attendances.user"])
    override fun findById(id: UUID): java.util.Optional<MeetingEntity>
}
