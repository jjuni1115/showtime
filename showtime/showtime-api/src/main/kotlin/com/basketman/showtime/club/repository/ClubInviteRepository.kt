package com.basketman.showtime.club.repository

import com.basketman.showtime.club.entity.ClubInviteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ClubInviteRepository : JpaRepository<ClubInviteEntity, java.util.UUID> {
    fun findByCode(code: String): ClubInviteEntity?
}
