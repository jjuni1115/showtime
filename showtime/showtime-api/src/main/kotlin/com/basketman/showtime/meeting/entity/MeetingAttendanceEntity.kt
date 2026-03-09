package com.basketman.showtime.meeting.entity

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
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "meeting_attendances")
class MeetingAttendanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    val meeting: MeetingEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: AppUserEntity? = null,

    @Column(name = "guest_name", length = 120)
    var guestName: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AttendanceStatus,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var source: AttendanceSource,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id", nullable = false)
    var updatedByUser: AppUserEntity,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
