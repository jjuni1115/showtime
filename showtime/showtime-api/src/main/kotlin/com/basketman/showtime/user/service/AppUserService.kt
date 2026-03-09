package com.basketman.showtime.user.service

import com.basketman.showtime.user.entity.AppUserEntity
import com.basketman.showtime.user.repository.AppUserRepository
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository,
) {
    fun getOrCreateFromGooglePrincipal(principal: OAuth2User): AppUserEntity {
        val provider = "google"
        val subject = principal.attributes["sub"]?.toString()
            ?: throw IllegalArgumentException("google subject(sub) is missing")
        val email = principal.attributes["email"]?.toString()
            ?: throw IllegalArgumentException("google email is missing")
        val name = principal.attributes["name"]?.toString()
            ?: email.substringBefore("@")

        val existing = appUserRepository.findByProviderAndProviderSubject(provider, subject)
        if (existing != null) {
            if (existing.email != email || existing.displayName != name) {
                existing.email = email
                existing.displayName = name
                return appUserRepository.save(existing)
            }
            return existing
        }

        return appUserRepository.save(
            AppUserEntity(
                provider = provider,
                providerSubject = subject,
                email = email,
                displayName = name,
            ),
        )
    }
}
