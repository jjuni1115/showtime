package com.basketman.showtime.club.entity

import com.basketman.showtime.user.entity.AppUserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "clubs")
class ClubEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 120)
    var name: String,

    @Column(name = "home_court", nullable = false, length = 255)
    var homeCourt: String,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    val ownerUser: AppUserEntity,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
