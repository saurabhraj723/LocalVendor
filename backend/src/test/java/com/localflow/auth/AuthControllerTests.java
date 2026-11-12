package com.localflow.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registersOwnerAndLogsIn() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String email = "owner-" + suffix + "@example.com";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "businessName": "Sharma Vegetables",
                                  "businessType": "VEGETABLE_VENDOR",
                                  "businessPhone": "+919999999999",
                                  "currency": "INR",
                                  "timezone": "Asia/Kolkata",
                                  "ownerName": "Saurabh",
                                  "ownerEmail": "%s",
                                  "password": "password123"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken", not(blankOrNullString())))
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.role").value("BUSINESS_OWNER"))
                .andExpect(jsonPath("$.business.name").value("Sharma Vegetables"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "password123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken", not(blankOrNullString())))
                .andExpect(jsonPath("$.business.name").value("Sharma Vegetables"));
    }

    @Test
    void rejectsProtectedApiWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/business"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void allowsLocalVendorWebsiteCorsPreflight() throws Exception {
        mockMvc.perform(options("/api/v1/orders")
                        .header("Origin", "http://localhost:4173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "authorization"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Origin", "http://localhost:4173"));
    }
}
