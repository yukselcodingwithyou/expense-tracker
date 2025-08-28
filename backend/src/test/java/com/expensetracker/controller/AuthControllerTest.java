package com.expensetracker.controller;

import com.expensetracker.dto.auth.AuthResponse;
import com.expensetracker.dto.auth.LoginRequest;
import com.expensetracker.dto.auth.SignupRequest;
import com.expensetracker.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signup_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("test@example.com", "password123");
        AuthResponse response = new AuthResponse("accessToken", "refreshToken", 900000L,
                new AuthResponse.UserInfo("user123", "test@example.com"));

        when(authService.signup(any(SignupRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void signup_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("invalid-email", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signup_ShouldReturnBadRequest_WhenPasswordIsTooShort() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("test@example.com", "123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnOk_WhenCredentialsAreValid() throws Exception {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse response = new AuthResponse("accessToken", "refreshToken", 900000L,
                new AuthResponse.UserInfo("user123", "test@example.com"));

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void logout_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk());
    }
}