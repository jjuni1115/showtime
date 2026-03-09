package com.basketman.showtime.member.repository

import com.basketman.showtime.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MemberRepository : JpaRepository<MemberEntity, UUID> {
    fun findAllByActiveTrueOrderByNameAsc(): List<MemberEntity>
}
