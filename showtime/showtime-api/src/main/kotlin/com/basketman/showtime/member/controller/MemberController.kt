package com.basketman.showtime.member.controller

import com.basketman.showtime.member.dto.CreateMemberRequest
import com.basketman.showtime.member.dto.MemberResponse
import com.basketman.showtime.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService,
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateMemberRequest): MemberResponse {
        return memberService.create(request)
    }

    @GetMapping
    fun getAll(): List<MemberResponse> {
        return memberService.getAllActive()
    }
}
