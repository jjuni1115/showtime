package com.basketman.showtime.community.controller

import com.basketman.showtime.auth.CurrentUserProvider
import com.basketman.showtime.community.dto.CreatePostRequest
import com.basketman.showtime.community.dto.PostResponse
import com.basketman.showtime.community.service.CommunityService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/posts")
class CommunityController(
    private val currentUserProvider: CurrentUserProvider,
    private val communityService: CommunityService,
) {
    @PostMapping
    fun createPost(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @Valid @RequestBody request: CreatePostRequest,
    ): PostResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return communityService.createPost(clubId, currentUser, request)
    }

    @GetMapping
    fun getPosts(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
        @RequestParam(defaultValue = "30") limit: Int,
    ): List<PostResponse> {
        val currentUser = currentUserProvider.resolve(principal)
        return communityService.getPosts(clubId, currentUser, limit)
    }
}
