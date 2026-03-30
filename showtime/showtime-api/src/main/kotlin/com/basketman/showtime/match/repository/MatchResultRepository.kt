package com.basketman.showtime.match.repository

import com.basketman.showtime.match.entity.MatchResultEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MatchResultRepository : JpaRepository<MatchResultEntity, UUID>
