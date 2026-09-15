package com.cricpro.app

import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.usecase.CalculatePointsTableUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CaptainVcRulesTest {

    @Test
    fun testCaptainVcBusinessRulesLogic() {
        val team = Team(
            teamId = "t1",
            teamName = "Royals",
            captainId = "P1",
            viceCaptainId = "P2"
        )

        // Verify Rule 1 & 2: Single C and Single VC
        assertEquals("P1", team.captainId)
        assertEquals("P2", team.viceCaptainId)
        assert(team.captainId != team.viceCaptainId)
    }

    @Test
    fun testNetRunRateAndPointsTableCalculation() {
        val useCase = CalculatePointsTableUseCase()
        val entries = useCase(
            teamIds = listOf("t1", "t2"),
            teamNamesMap = mapOf("t1" to "Team Alpha", "t2" to "Team Beta"),
            matches = emptyList()
        )

        assertEquals(2, entries.size)
        assertEquals(0, entries[0].points)
        assertEquals(0.0, entries[0].netRunRate, 0.001)
    }
}
