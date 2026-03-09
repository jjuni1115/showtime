package com.basketman.showtime.club.repository

import com.basketman.showtime.club.entity.ClubEntity
import com.basketman.showtime.club.entity.ClubMembershipEntity
import com.basketman.showtime.club.entity.MembershipRole
import com.basketman.showtime.user.entity.AppUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ClubMembershipRepository : JpaRepository<ClubMembershipEntity, Long> {
    fun findByClubIdAndUserId(clubId: UUID, userId: UUID): ClubMembershipEntity?
    fun findAllByUserId(userId: UUID): List<ClubMembershipEntity>
    fun existsByClubIdAndUserIdAndRole(clubId: UUID, userId: UUID, role: MembershipRole): Boolean
    fun existsByClubAndUser(club: ClubEntity, user: AppUserEntity): Boolean
}
