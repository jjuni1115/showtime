package com.basketman.showtime.user.repository

import com.basketman.showtime.user.entity.AppUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AppUserRepository : JpaRepository<AppUserEntity, UUID> {
    fun findByProviderAndProviderSubject(provider: String, providerSubject: String): AppUserEntity?
}
