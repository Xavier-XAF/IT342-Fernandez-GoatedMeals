package edu.cit.fernandez.goatedmeals.mobile.api

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthDataPayload? // We look for the "data" object now
)

// We create a nested class to grab the token from inside the "data" object
data class AuthDataPayload(
    @SerializedName("accessToken") // This tells Gson to look for "accessToken" from Spring Boot
    val token: String?, // But we can still just call it "token" in our Kotlin code
    val user: UserLoginDto?
)

data class UserLoginDto(
    val email: String?,
    val role: String?
)