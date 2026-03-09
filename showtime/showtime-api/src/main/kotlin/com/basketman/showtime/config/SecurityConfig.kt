package com.basketman.showtime.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/ping",
                    "/actuator/health",
                    "/error",
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login(Customizer.withDefaults())

        return http.build()
    }
}
