package com.basketman.showtime.member.service

import com.basketman.showtime.member.dto.CreateMemberRequest
import com.basketman.showtime.member.dto.MemberResponse
import com.basketman.showtime.member.entity.MemberEntity
import com.basketman.showtime.member.repository.MemberRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun create(request: CreateMemberRequest): MemberResponse {
        val member = memberRepository.save(
            MemberEntity(
                name = request.name.trim(),
                heightCm = request.heightCm,
                skillLevel = request.skillLevel,
                position = request.position,
                style = request.style,
            ),
        )
        return member.toResponse()
    }

    fun getAllActive(): List<MemberResponse> {
        return memberRepository.findAllByActiveTrueOrderByNameAsc().map { it.toResponse() }
    }

    fun getAllActiveEntitiesByIds(ids: Collection<UUID>): List<MemberEntity> {
        val byId = memberRepository.findAllById(ids).associateBy { it.id }
        return ids.mapNotNull { id ->
            val member = byId[id]
            if (member?.active == true) member else null
        }
    }

    private fun MemberEntity.toResponse(): MemberResponse {
        return MemberResponse(
            id = id,
            name = name,
            heightCm = heightCm,
            skillLevel = skillLevel,
            position = position,
            style = style,
            active = active,
        )
    }
}
