package com.basketman.showtime.team.controller

import com.basketman.showtime.team.dto.TeamGenerateRequest
import com.basketman.showtime.team.dto.TeamGenerateResponse
import com.basketman.showtime.team.dto.TeamFromAttendanceRequest
import com.basketman.showtime.team.service.TeamBalancerService
import com.basketman.showtime.team.service.TeamGenerationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/teams")
class TeamController(
    private val teamBalancerService: TeamBalancerService,
    private val teamGenerationService: TeamGenerationService,
) {
    @PostMapping("/generate")
    fun generate(@Valid @RequestBody request: TeamGenerateRequest): TeamGenerateResponse {
        return teamBalancerService.generate(request)
    }

    @PostMapping("/generate-from-attendance")
    fun generateFromAttendance(@Valid @RequestBody request: TeamFromAttendanceRequest): TeamGenerateResponse {
        return teamGenerationService.generateFromAttendance(request)
    }
}
