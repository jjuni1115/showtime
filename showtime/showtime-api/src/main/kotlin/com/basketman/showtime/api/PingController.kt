package com.basketman.showtime.api

import com.basketman.showtime.time.TimeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class PingController(
    private val timeService: TimeService,
) {
    @GetMapping("/ping")
    fun ping(): PingResponse = PingResponse(
        message = "pong",
        timestamp = timeService.now(),
    )
}
