package com.basketman.showtime.attendance.service

import com.basketman.showtime.attendance.dto.AttendanceResponse
import com.basketman.showtime.attendance.dto.UpsertAttendanceRequest
import com.basketman.showtime.attendance.entity.AttendanceEntity
import com.basketman.showtime.attendance.repository.AttendanceRepository
import com.basketman.showtime.member.dto.MemberResponse
import com.basketman.showtime.member.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val memberService: MemberService,
) {
    @Transactional
    fun upsert(date: LocalDate, request: UpsertAttendanceRequest): AttendanceResponse {
        val uniqueMemberIds = request.memberIds.distinct()
        val members = memberService.getAllActiveEntitiesByIds(uniqueMemberIds)
        if (members.size != uniqueMemberIds.size) {
            throw IllegalArgumentException("some memberIds are invalid or inactive")
        }

        attendanceRepository.deleteAllByAttendanceDate(date)
        val saved = attendanceRepository.saveAll(
            members.map { member ->
                AttendanceEntity(attendanceDate = date, member = member)
            },
        )

        val attendees = saved.map { it.member.toResponse() }.sortedBy { it.name }
        return AttendanceResponse(date = date, attendees = attendees)
    }

    @Transactional(readOnly = true)
    fun getByDate(date: LocalDate): AttendanceResponse {
        val attendees = attendanceRepository.findAllByAttendanceDate(date)
            .map { it.member.toResponse() }
            .sortedBy { it.name }
        return AttendanceResponse(date = date, attendees = attendees)
    }

    @Transactional(readOnly = true)
    fun getAttendeeMemberIds(date: LocalDate): List<UUID> {
        return attendanceRepository.findAllByAttendanceDate(date).map { it.member.id }
    }

    private fun com.basketman.showtime.member.entity.MemberEntity.toResponse(): MemberResponse {
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
