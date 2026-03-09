package com.basketman.showtime.club.repository

import com.basketman.showtime.club.entity.ClubEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClubRepository : JpaRepository<ClubEntity, UUID>
