package com.expensetracker.data.repository

import com.expensetracker.data.local.TokenManager
import com.expensetracker.data.model.AuthResponse
import com.expensetracker.data.model.LoginRequest
import com.expensetracker.data.model.SignupRequest
import com.expensetracker.data.model.User
import com.expensetracker.data.network.AuthApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {

    val isAuthenticated: Flow<Boolean> = tokenManager.accessToken.map { token ->
        !token.isNullOrBlank()
    }

    val currentUser: Flow<User?> = tokenManager.userId.map { userId ->
        if (userId != null) {
            // For now, return a basic user. In a full implementation, you might cache user data
            User(userId, tokenManager.userEmail.first() ?: "")
        } else {
            null
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken,
                    authResponse.user.id,
                    authResponse.user.email
                )
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApiService.signup(SignupRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                tokenManager.saveTokens(
                    authResponse.accessToken,
                    authResponse.refreshToken,
                    authResponse.user.id,
                    authResponse.user.email
                )
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Signup failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            // Call logout API to blacklist token
            authApiService.logout()
            // Clear local tokens
            tokenManager.clearTokens()
            Result.success(Unit)
        } catch (e: Exception) {
            // Even if API call fails, clear local tokens
            tokenManager.clearTokens()
            Result.success(Unit)
        }
    }

    suspend fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }
}