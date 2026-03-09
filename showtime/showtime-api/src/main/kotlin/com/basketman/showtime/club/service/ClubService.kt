package com.basketman.showtime.club.service

import com.basketman.showtime.club.dto.ClubResponse
import com.basketman.showtime.club.dto.CreateClubRequest
import com.basketman.showtime.club.dto.InviteResponse
import com.basketman.showtime.club.entity.ClubEntity
import com.basketman.showtime.club.entity.ClubInviteEntity
import com.basketman.showtime.club.entity.ClubMembershipEntity
import com.basketman.showtime.club.entity.MembershipRole
import com.basketman.showtime.club.repository.ClubInviteRepository
import com.basketman.showtime.club.repository.ClubMembershipRepository
import com.basketman.showtime.club.repository.ClubRepository
import com.basketman.showtime.user.entity.AppUserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class ClubService(
    private val clubRepository: ClubRepository,
    private val clubMembershipRepository: ClubMembershipRepository,
    private val clubInviteRepository: ClubInviteRepository,
    @Value("\${app.invite.base-url}")
    private val inviteBaseUrl: String,
    @Value("\${app.invite.expires-hours}")
    private val inviteExpiresHours: Long,
) {
    @Transactional
    fun createClub(currentUser: AppUserEntity, request: CreateClubRequest): ClubResponse {
        val club = clubRepository.save(
            ClubEntity(
                name = request.name.trim(),
                homeCourt = request.homeCourt.trim(),
                imageUrl = request.imageUrl?.trim()?.ifBlank { null },
                ownerUser = currentUser,
            ),
        )

        clubMembershipRepository.save(
            ClubMembershipEntity(
                club = club,
                user = currentUser,
                role = MembershipRole.ADMIN,
            ),
        )

        return club.toResponse(MembershipRole.ADMIN)
    }

    @Transactional(readOnly = true)
    fun getMyClubs(currentUser: AppUserEntity): List<ClubResponse> {
        return clubMembershipRepository.findAllByUserId(currentUser.id)
            .map { membership -> membership.club.toResponse(membership.role) }
            .sortedBy { it.name }
    }

    @Transactional
    fun createInvite(currentUser: AppUserEntity, clubId: UUID): InviteResponse {
        ensureAdmin(clubId, currentUser.id)

        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val code = UUID.randomUUID().toString().replace("-", "")
        val expiresAt = Instant.now().plus(inviteExpiresHours, ChronoUnit.HOURS)

        val invite = clubInviteRepository.save(
            ClubInviteEntity(
                club = club,
                code = code,
                createdByUser = currentUser,
                expiresAt = expiresAt,
            ),
        )

        return InviteResponse(
            code = invite.code,
            inviteLink = "${inviteBaseUrl}?code=${invite.code}",
            expiresAt = invite.expiresAt,
        )
    }

    @Transactional
    fun joinByInviteCode(currentUser: AppUserEntity, code: String): ClubResponse {
        val invite = clubInviteRepository.findByCode(code.trim())
            ?: throw IllegalArgumentException("invalid invite code")

        if (!invite.active) {
            throw IllegalArgumentException("invite is inactive")
        }
        if (invite.expiresAt != null && invite.expiresAt!!.isBefore(Instant.now())) {
            throw IllegalArgumentException("invite has expired")
        }

        if (!clubMembershipRepository.existsByClubAndUser(invite.club, currentUser)) {
            clubMembershipRepository.save(
                ClubMembershipEntity(
                    club = invite.club,
                    user = currentUser,
                    role = MembershipRole.MEMBER,
                ),
            )
        }

        val membership = clubMembershipRepository.findByClubIdAndUserId(invite.club.id, currentUser.id)
            ?: throw IllegalStateException("membership creation failed")

        return invite.club.toResponse(membership.role)
    }

    @Transactional(readOnly = true)
    fun ensureMember(clubId: UUID, userId: UUID): MembershipRole {
        val membership = clubMembershipRepository.findByClubIdAndUserId(clubId, userId)
            ?: throw IllegalArgumentException("not a member of this club")
        return membership.role
    }

    @Transactional(readOnly = true)
    fun ensureAdmin(clubId: UUID, userId: UUID) {
        val isAdmin = clubMembershipRepository.existsByClubIdAndUserIdAndRole(clubId, userId, MembershipRole.ADMIN)
        if (!isAdmin) {
            throw IllegalArgumentException("admin permission required")
        }
    }

    private fun ClubEntity.toResponse(role: MembershipRole): ClubResponse {
        return ClubResponse(
            id = id,
            name = name,
            homeCourt = homeCourt,
            imageUrl = imageUrl,
            myRole = role,
        )
    }
}
