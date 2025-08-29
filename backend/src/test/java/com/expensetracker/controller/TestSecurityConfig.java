package com.expensetracker.controller;

import com.expensetracker.security.JwtTokenProvider;
import com.expensetracker.service.TokenBlacklistService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        return mock(RedisTemplate.class);
    }

    @Bean
    public TokenBlacklistService tokenBlacklistService() {
        return mock(TokenBlacklistService.class);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(
            "testSecretKeyForJWTTokenGenerationThatShouldBeStoredSecurely",
            900000L,
            2592000000L
        );
    }
}