package com.basketman.showtime.match.controller

import com.basketman.showtime.match.dto.CreateMatchResultRequest
import com.basketman.showtime.match.dto.MatchResultResponse
import com.basketman.showtime.match.dto.VideoMetadataResponse
import com.basketman.showtime.match.service.MatchService
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1")
class MatchController(
    private val matchService: MatchService,
) {
    @PostMapping("/matches")
    fun createMatchResult(@Valid @RequestBody request: CreateMatchResultRequest): MatchResultResponse {
        return matchService.createResult(request)
    }

    @GetMapping("/matches")
    fun getMatchResults(): List<MatchResultResponse> {
        return matchService.getResults()
    }

    @GetMapping("/matches/{matchId}")
    fun getMatchResult(@PathVariable matchId: String): MatchResultResponse {
        return matchService.getResult(matchId)
    }

    @PostMapping("/matches/{matchId}/videos")
    fun uploadVideo(
        @PathVariable matchId: String,
        @RequestParam("file") file: MultipartFile,
    ): VideoMetadataResponse {
        return matchService.uploadVideo(matchId, file)
    }

    @GetMapping("/videos/{videoId}/download")
    fun downloadVideo(@PathVariable videoId: String): ResponseEntity<Resource> {
        val downloadable = matchService.loadVideo(videoId)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(downloadable.contentType))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(downloadable.fileName).build().toString(),
            )
            .body(downloadable.resource)
    }
}
