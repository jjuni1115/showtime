package com.basketman.showtime

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShowtimeApplication

fun main(args: Array<String>) {
    runApplication<ShowtimeApplication>(*args)
}
