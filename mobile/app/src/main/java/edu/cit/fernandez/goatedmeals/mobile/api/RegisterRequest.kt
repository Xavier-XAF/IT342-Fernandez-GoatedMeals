package edu.cit.fernandez.goatedmeals.mobile.api

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val contactNumber: String
)