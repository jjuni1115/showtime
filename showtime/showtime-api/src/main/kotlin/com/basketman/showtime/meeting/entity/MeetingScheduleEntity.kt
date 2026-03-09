package com.basketman.showtime.meeting.entity

import com.basketman.showtime.club.entity.ClubEntity
import com.basketman.showtime.user.entity.AppUserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime

@Entity
@Table(name = "meeting_schedules")
class MeetingScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, unique = true)
    val club: ClubEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    var dayOfWeek: DayOfWeek,

    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    var updatedByUser: AppUserEntity,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
