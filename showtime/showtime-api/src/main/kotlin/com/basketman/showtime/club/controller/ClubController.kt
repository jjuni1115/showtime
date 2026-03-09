package com.basketman.showtime.club.controller

import com.basketman.showtime.auth.CurrentUserProvider
import com.basketman.showtime.club.dto.ClubResponse
import com.basketman.showtime.club.dto.CreateClubRequest
import com.basketman.showtime.club.dto.InviteResponse
import com.basketman.showtime.club.dto.JoinClubRequest
import com.basketman.showtime.club.service.ClubService
import jakarta.validation.Valid
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/clubs")
class ClubController(
    private val currentUserProvider: CurrentUserProvider,
    private val clubService: ClubService,
) {
    @PostMapping
    fun createClub(
        @AuthenticationPrincipal principal: OAuth2User,
        @Valid @RequestBody request: CreateClubRequest,
    ): ClubResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return clubService.createClub(currentUser, request)
    }

    @GetMapping("/my")
    fun getMyClubs(@AuthenticationPrincipal principal: OAuth2User): List<ClubResponse> {
        val currentUser = currentUserProvider.resolve(principal)
        return clubService.getMyClubs(currentUser)
    }

    @PostMapping("/{clubId}/invites")
    fun createInvite(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable clubId: UUID,
    ): InviteResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return clubService.createInvite(currentUser, clubId)
    }

    @PostMapping("/join")
    fun joinByInvite(
        @AuthenticationPrincipal principal: OAuth2User,
        @Valid @RequestBody request: JoinClubRequest,
    ): ClubResponse {
        val currentUser = currentUserProvider.resolve(principal)
        return clubService.joinByInviteCode(currentUser, request.code)
    }
}
