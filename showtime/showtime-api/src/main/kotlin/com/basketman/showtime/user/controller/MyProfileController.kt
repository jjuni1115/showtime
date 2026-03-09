package com.basketman.showtime.user.controller

import com.basketman.showtime.auth.CurrentUserProvider
import com.basketman.showtime.user.dto.MyProfileResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class MyProfileController(
    private val currentUserProvider: CurrentUserProvider,
) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal principal: OAuth2User): MyProfileResponse {
        val user = currentUserProvider.resolve(principal)
        return MyProfileResponse(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
        )
    }
}
