package edu.cit.fernandez.goatedmeals.mobile.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>
}