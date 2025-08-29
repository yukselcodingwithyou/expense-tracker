package com.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignupRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val families: List<FamilyMembership> = emptyList(),
    val preferredFamilyId: String? = null
)

@Serializable
data class FamilyMembership(
    val familyId: String,
    val role: String // "ADMIN" or "MEMBER"
)