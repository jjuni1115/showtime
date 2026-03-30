package com.basketman.showtime.meeting.entity

import com.basketman.showtime.club.entity.ClubEntity
import com.basketman.showtime.user.entity.AppUserEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity
@Table(name = "meetings")
class MeetingEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    val club: ClubEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    var schedule: MeetingScheduleEntity? = null,

    @Column(name = "meeting_date", nullable = false)
    var meetingDate: LocalDate,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(name = "place", length = 255)
    var place: String? = null,

    @Column(columnDefinition = "text")
    var note: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    val createdByUser: AppUserEntity,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "meeting", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("updatedAt DESC")
    val attendances: MutableList<MeetingAttendanceEntity> = mutableListOf(),
)
