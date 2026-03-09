package com.basketman.showtime.api

import java.time.Instant

data class PingResponse(
    val message: String,
    val timestamp: Instant,
)
