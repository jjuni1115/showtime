package com.basketman.showtime.team.service

import com.basketman.showtime.team.PlayStyle
import com.basketman.showtime.team.Position
import com.basketman.showtime.team.SkillLevel
import com.basketman.showtime.team.dto.MemberProfile
import com.basketman.showtime.team.dto.TeamGenerateRequest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TeamBalancerServiceTests {
    private val service = TeamBalancerService()

    @Test
    fun `distributes members evenly across teams`() {
        val request = TeamGenerateRequest(
            teamNames = listOf("Blue", "Black", "White"),
            attendees = sampleMembers(),
        )

        val result = service.generate(request)
        val sizes = result.teams.map { it.members.size }

        assertTrue(sizes.max() - sizes.min() <= 1)
        assertTrue(result.summary.teamSizeGap <= 1)
    }

    @Test
    fun `penalizes repeated team assignments from previous week`() {
        val members = sampleMembers()
        val request = TeamGenerateRequest(
            teamNames = listOf("Blue", "Black", "White"),
            attendees = members,
            previousTeamByMemberId = members.associate { it.id to "Blue" },
        )

        val result = service.generate(request)
        val repeated = result.teams.first { it.name == "Blue" }.repeatedFromLastWeekCount

        assertTrue(repeated < members.size)
    }

    private fun sampleMembers(): List<MemberProfile> {
        return listOf(
            MemberProfile("m1", "A", 182, SkillLevel.HIGH, Position.GUARD, PlayStyle.PLAYMAKER),
            MemberProfile("m2", "B", 190, SkillLevel.HIGH, Position.FORWARD, PlayStyle.SLASHER),
            MemberProfile("m3", "C", 197, SkillLevel.HIGH, Position.CENTER, PlayStyle.REBOUNDER),
            MemberProfile("m4", "D", 177, SkillLevel.UPPER_MID, Position.GUARD, PlayStyle.SHOOTER),
            MemberProfile("m5", "E", 185, SkillLevel.UPPER_MID, Position.FORWARD, PlayStyle.DEFENDER),
            MemberProfile("m6", "F", 193, SkillLevel.UPPER_MID, Position.CENTER, PlayStyle.BALANCED),
            MemberProfile("m7", "G", 175, SkillLevel.MID, Position.GUARD, PlayStyle.SLASHER),
            MemberProfile("m8", "H", 188, SkillLevel.MID, Position.FORWARD, PlayStyle.REBOUNDER),
            MemberProfile("m9", "I", 199, SkillLevel.MID, Position.CENTER, PlayStyle.DEFENDER),
            MemberProfile("m10", "J", 180, SkillLevel.LOWER_MID, Position.GUARD, PlayStyle.SHOOTER),
            MemberProfile("m11", "K", 186, SkillLevel.LOWER_MID, Position.FORWARD, PlayStyle.BALANCED),
            MemberProfile("m12", "L", 194, SkillLevel.LOWER_MID, Position.CENTER, PlayStyle.REBOUNDER),
            MemberProfile("m13", "M", 178, SkillLevel.LOW, Position.GUARD, PlayStyle.PLAYMAKER),
            MemberProfile("m14", "N", 184, SkillLevel.LOW, Position.FORWARD, PlayStyle.SLASHER),
            MemberProfile("m15", "O", 192, SkillLevel.LOW, Position.CENTER, PlayStyle.DEFENDER),
        )
    }
}
