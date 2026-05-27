package edu.cit.fernandez.goatedmeals.mobile.api

data class LoginRequest(
    val email: String? = null,
    val password: String? = null,

    // The fields for Google Strategy
    val loginType: String = "STANDARD",
    val googleIdToken: String? = null
)