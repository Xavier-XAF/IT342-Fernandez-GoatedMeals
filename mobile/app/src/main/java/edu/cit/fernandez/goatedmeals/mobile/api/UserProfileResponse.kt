package edu.cit.fernandez.goatedmeals.mobile.api

// This perfectly matches your backend UserResponseDTO!
data class UserProfileResponse(
    val id: Long,
    val email: String,
    val firstname: String?,
    val lastname: String?,
    val role: String
)