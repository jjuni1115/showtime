package com.basketman.showtime.member.entity

import com.basketman.showtime.team.PlayStyle
import com.basketman.showtime.team.Position
import com.basketman.showtime.team.SkillLevel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "members")
class MemberEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(name = "height_cm", nullable = false)
    var heightCm: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false, length = 20)
    var skillLevel: SkillLevel,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var position: Position,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var style: PlayStyle,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}
