package edu.cit.fernandez.goatedmeals.mobile.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Retrieve the token we saved in MainActivity
        val sharedPreferences = context.getSharedPreferences("GoatedMealsPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        // If a token exists, add it to the Authorization header
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}