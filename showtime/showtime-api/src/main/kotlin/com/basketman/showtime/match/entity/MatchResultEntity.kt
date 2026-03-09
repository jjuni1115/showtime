package com.basketman.showtime.match.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "match_results")
class MatchResultEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "played_at", nullable = false)
    var playedAt: LocalDate,

    @Column(columnDefinition = "text")
    var memo: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "match", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    val teamScores: MutableList<MatchTeamScoreEntity> = mutableListOf(),

    @OneToMany(mappedBy = "match", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("uploadedAt DESC")
    val videos: MutableList<MatchVideoEntity> = mutableListOf(),
)
