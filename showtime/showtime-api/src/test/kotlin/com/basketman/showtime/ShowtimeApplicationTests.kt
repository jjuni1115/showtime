package com.basketman.showtime

import com.basketman.showtime.api.PingController
import com.basketman.showtime.time.TimeService
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ShowtimeApplicationTests {
    @Test
    fun `ping endpoint returns pong`() {
        val mockMvc = MockMvcBuilders.standaloneSetup(PingController(TimeService())).build()

        mockMvc.perform(get("/api/v1/ping"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("pong"))
            .andExpect(jsonPath("$.timestamp").exists())
    }
}
