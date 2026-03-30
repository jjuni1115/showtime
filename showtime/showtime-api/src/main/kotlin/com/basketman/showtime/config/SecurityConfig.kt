package com.basketman.showtime.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
class SecurityConfig(
    private val environment: Environment,
) {
    private val loginSuccessRedirectUrl: String
        get() = environment.getProperty("app.auth.login-success-redirect-url") ?: "http://localhost:5173"

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/ping",
                    "/actuator/health",
                    "/error",
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.defaultAuthenticationEntryPointFor(
                    HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    RequestMatcher { request -> request.requestURI.startsWith("/api/") },
                )
            }
            .oauth2Login {
                it.defaultSuccessUrl(loginSuccessRedirectUrl, true)
            }

        return http.build()
    }
}
