package com.basketman.showtime.community.service

import com.basketman.showtime.club.repository.ClubRepository
import com.basketman.showtime.club.service.ClubService
import com.basketman.showtime.community.dto.CreatePostRequest
import com.basketman.showtime.community.dto.PostResponse
import com.basketman.showtime.community.entity.ClubPostEntity
import com.basketman.showtime.community.repository.ClubPostRepository
import com.basketman.showtime.user.entity.AppUserEntity
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class CommunityService(
    private val clubService: ClubService,
    private val clubRepository: ClubRepository,
    private val clubPostRepository: ClubPostRepository,
) {
    @Transactional
    fun createPost(clubId: UUID, currentUser: AppUserEntity, request: CreatePostRequest): PostResponse {
        clubService.ensureMember(clubId, currentUser.id)
        val club = clubRepository.findById(clubId)
            .orElseThrow { IllegalArgumentException("club not found: $clubId") }

        val saved = clubPostRepository.save(
            ClubPostEntity(
                club = club,
                authorUser = currentUser,
                content = request.content.trim(),
            ),
        )

        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getPosts(clubId: UUID, currentUser: AppUserEntity, limit: Int): List<PostResponse> {
        clubService.ensureMember(clubId, currentUser.id)
        val safeLimit = limit.coerceIn(1, 100)
        return clubPostRepository.findAllByClubIdOrderByCreatedAtDesc(clubId, PageRequest.of(0, safeLimit))
            .map { it.toResponse() }
    }

    private fun ClubPostEntity.toResponse(): PostResponse {
        return PostResponse(
            id = id,
            clubId = club.id,
            authorUserId = authorUser.id,
            authorName = authorUser.displayName,
            content = content,
            createdAt = createdAt,
        )
    }
}
