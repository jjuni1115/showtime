package com.basketman.showtime.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "app_users")
class AppUserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 30)
    val provider: String,

    @Column(name = "provider_subject", nullable = false, length = 255)
    val providerSubject: String,

    @Column(nullable = false, length = 255)
    var email: String,

    @Column(name = "display_name", nullable = false, length = 120)
    var displayName: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
