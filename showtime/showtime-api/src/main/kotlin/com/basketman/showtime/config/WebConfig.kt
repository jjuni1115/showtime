package com.basketman.showtime.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val environment: Environment,
) : WebMvcConfigurer {
    private val allowedOrigins: Array<String>
        get() = environment.getProperty("app.cors.allowed-origins", Array<String>::class.java)
            ?: arrayOf("http://localhost:5173")

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}
