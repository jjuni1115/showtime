package com.basketman.showtime.match.repository

import com.basketman.showtime.match.entity.MatchResultEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MatchResultRepository : JpaRepository<MatchResultEntity, UUID> {
    @EntityGraph(attributePaths = ["teamScores", "videos"])
    override fun findAll(): List<MatchResultEntity>

    @EntityGraph(attributePaths = ["teamScores", "videos"])
    override fun findById(id: UUID): java.util.Optional<MatchResultEntity>
}
