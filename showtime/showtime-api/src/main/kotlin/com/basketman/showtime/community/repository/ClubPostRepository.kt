package com.basketman.showtime.community.repository

import com.basketman.showtime.community.entity.ClubPostEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClubPostRepository : JpaRepository<ClubPostEntity, UUID> {
    fun findAllByClubIdOrderByCreatedAtDesc(clubId: UUID, pageable: Pageable): List<ClubPostEntity>
}
