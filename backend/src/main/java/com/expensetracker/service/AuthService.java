package com.expensetracker.service;

import com.expensetracker.domain.User;
import com.expensetracker.dto.auth.AuthResponse;
import com.expensetracker.dto.auth.LoginRequest;
import com.expensetracker.dto.auth.SignupRequest;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.security.JwtTokenProvider;
import com.expensetracker.security.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getEmail(), encodedPassword);
        user = userRepository.save(user);

        return generateAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(user.getId(), user.getEmail());
        
        return new AuthResponse(accessToken, refreshToken, tokenProvider.getAccessTokenExpiration(), userInfo);
    }
}