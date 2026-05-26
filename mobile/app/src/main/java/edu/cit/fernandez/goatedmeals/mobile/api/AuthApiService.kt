package edu.cit.fernandez.goatedmeals.mobile.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("auth/login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>

    // Fetch the entire meal catalog
    @GET("meals") // Note: If your Spring Boot endpoint is something like "/api/v1/menu", change "meals" to match!
    fun getMealCatalog(): Call<List<Meal>>

    // Book a meal
    @POST("schedules/book")
    fun bookMeal(@Body request: ScheduleRequest): Call<ScheduleResponse>

    // Check current subscription status
    @GET("subscriptions/me")
    fun getMySubscription(): Call<SubscriptionResponse>

    // Generate PayMongo Checkout URL
    @POST("subscriptions/pay")
    fun initiatePayment(@Body request: PaymentRequest): Call<PaymentResponse>

    @GET("schedules/my-schedule")
    fun getMySchedules(): Call<List<MealSchedule>>

    @DELETE("schedules/{id}")
    fun cancelSchedule(@Path("id") scheduleId: Long): Call<CancelResponse>

    // Fetch Current Logged-In User Profile
    @GET("auth/me")
    fun getCurrentUser(): Call<UserProfileResponse>

    @POST("auth/google")
    fun googleLogin(@Body request: GoogleLoginRequest): Call<AuthResponse>




}