package com.basketman.showtime.match.service

import com.basketman.showtime.match.dto.CreateMatchResultRequest
import com.basketman.showtime.match.dto.MatchResultResponse
import com.basketman.showtime.match.dto.VideoMetadataResponse
import com.basketman.showtime.match.entity.MatchResultEntity
import com.basketman.showtime.match.entity.MatchTeamScoreEntity
import com.basketman.showtime.match.entity.MatchVideoEntity
import com.basketman.showtime.match.repository.MatchResultRepository
import com.basketman.showtime.match.repository.MatchVideoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.util.UUID

@Service
class MatchService(
    @Value("\${app.video.storage-dir}")
    storageDir: String,
    private val matchResultRepository: MatchResultRepository,
    private val matchVideoRepository: MatchVideoRepository,
) {
    private val baseDir: Path = Path.of(storageDir).toAbsolutePath().normalize()

    init {
        Files.createDirectories(baseDir)
    }

    @Transactional
    fun createResult(request: CreateMatchResultRequest): MatchResultResponse {
        val match = MatchResultEntity(
            playedAt = request.playedAt,
            memo = request.memo,
        )

        request.teamScores.forEach { (teamName, score) ->
            match.teamScores.add(
                MatchTeamScoreEntity(
                    match = match,
                    teamName = teamName,
                    score = score,
                ),
            )
        }

        val saved = matchResultRepository.save(match)
        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun getResults(): List<MatchResultResponse> {
        return matchResultRepository.findAll()
            .sortedByDescending { it.playedAt }
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getResult(matchId: String): MatchResultResponse {
        val id = toUuid(matchId)
        return findMatch(id).toResponse()
    }

    @Transactional
    fun uploadVideo(matchId: String, file: MultipartFile): VideoMetadataResponse {
        require(!file.isEmpty) { "video file is empty" }

        val id = toUuid(matchId)
        val match = findMatch(id)
        val videoId = UUID.randomUUID()
        val safeFileName = (file.originalFilename ?: "video.mp4").replace(Regex("[^A-Za-z0-9._-]"), "_")
        val savedName = "${videoId}_$safeFileName"

        val matchDir = baseDir.resolve(match.id.toString())
        Files.createDirectories(matchDir)

        val targetPath = matchDir.resolve(savedName)
        file.inputStream.use { input ->
            Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }

        val video = MatchVideoEntity(
            id = videoId,
            match = match,
            fileName = safeFileName,
            contentType = file.contentType ?: "application/octet-stream",
            sizeBytes = Files.size(targetPath),
            filePath = targetPath.toString(),
            uploadedAt = Instant.now(),
        )
        val saved = matchVideoRepository.save(video)

        return saved.toResponse()
    }

    @Transactional(readOnly = true)
    fun loadVideo(videoId: String): DownloadableVideo {
        val id = toUuid(videoId)
        val video = matchVideoRepository.findById(id)
            .orElseThrow { IllegalArgumentException("video not found: $videoId") }

        val path = Path.of(video.filePath)
        val resource: Resource = UrlResource(path.toUri())
        if (!resource.exists()) {
            throw IllegalArgumentException("video file not found: $videoId")
        }

        return DownloadableVideo(
            resource = resource,
            contentType = video.contentType,
            fileName = video.fileName,
        )
    }

    private fun findMatch(id: UUID): MatchResultEntity {
        return matchResultRepository.findById(id)
            .orElseThrow { IllegalArgumentException("match not found: $id") }
    }

    private fun MatchResultEntity.toResponse(): MatchResultResponse {
        return MatchResultResponse(
            id = id.toString(),
            playedAt = playedAt,
            teamScores = teamScores.associate { it.teamName to it.score },
            memo = memo,
            videos = videos.map { it.toResponse() },
            createdAt = createdAt,
        )
    }

    private fun MatchVideoEntity.toResponse(): VideoMetadataResponse {
        return VideoMetadataResponse(
            id = id.toString(),
            fileName = fileName,
            contentType = contentType,
            sizeBytes = sizeBytes,
            uploadedAt = uploadedAt,
            downloadUrl = "/api/v1/videos/$id/download",
        )
    }

    private fun toUuid(value: String): UUID {
        return try {
            UUID.fromString(value)
        } catch (_: IllegalArgumentException) {
            throw IllegalArgumentException("invalid id format: $value")
        }
    }
}

data class DownloadableVideo(
    val resource: Resource,
    val contentType: String,
    val fileName: String,
)
