package com.project.monitoring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnActiveIncidents() throws Exception {
        mockMvc.perform(get("/api/dashboard/active-incidents"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnServicesHealth() throws Exception {
        mockMvc.perform(get("/api/dashboard/services-health"))
                .andExpect(status().isOk());
    }
}