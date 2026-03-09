package com.basketman.showtime.time

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TimeService {
    fun now(): Instant = Instant.now()
}
