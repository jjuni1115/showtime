package com.basketman.showtime.match.entity

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
@Table(name = "match_videos")
class MatchVideoEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    val match: MatchResultEntity,

    @Column(name = "file_name", nullable = false, length = 255)
    val fileName: String,

    @Column(name = "content_type", nullable = false, length = 150)
    val contentType: String,

    @Column(name = "size_bytes", nullable = false)
    val sizeBytes: Long,

    @Column(name = "file_path", nullable = false, length = 1000)
    val filePath: String,

    @Column(name = "uploaded_at", nullable = false)
    val uploadedAt: Instant = Instant.now(),
)
