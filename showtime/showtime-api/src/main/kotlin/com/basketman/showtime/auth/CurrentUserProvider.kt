package com.basketman.showtime.auth

import com.basketman.showtime.user.entity.AppUserEntity
import com.basketman.showtime.user.service.AppUserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class CurrentUserProvider(
    private val appUserService: AppUserService,
) {
    fun resolve(principal: OAuth2User): AppUserEntity {
        return appUserService.getOrCreateFromGooglePrincipal(principal)
    }
}
