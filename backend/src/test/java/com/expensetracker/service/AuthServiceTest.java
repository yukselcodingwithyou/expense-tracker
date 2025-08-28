package com.expensetracker.service;

import com.expensetracker.domain.User;
import com.expensetracker.dto.auth.AuthResponse;
import com.expensetracker.dto.auth.LoginRequest;
import com.expensetracker.dto.auth.SignupRequest;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtTokenProvider;
import com.expensetracker.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
        testUser = new User("test@example.com", "hashedPassword");
        testUser.setId("user123");
    }

    @Test
    void signup_ShouldCreateUserAndReturnAuthResponse() {
        // Given
        when(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenProvider.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(tokenProvider.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.signup(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("test@example.com", response.getUser().getEmail());
        
        verify(userRepository).existsByEmailAndDeletedAtIsNull("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmailAndDeletedAtIsNull(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.signup(signupRequest));
        assertEquals("Email already exists", exception.getMessage());
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenProvider.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(tokenProvider.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("test@example.com", response.getUser().getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        // Given
        when(userRepository.findByEmailAndDeletedAtIsNull(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}