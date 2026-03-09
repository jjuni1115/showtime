package com.basketman.showtime.match.repository

import com.basketman.showtime.match.entity.MatchVideoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MatchVideoRepository : JpaRepository<MatchVideoEntity, UUID>
