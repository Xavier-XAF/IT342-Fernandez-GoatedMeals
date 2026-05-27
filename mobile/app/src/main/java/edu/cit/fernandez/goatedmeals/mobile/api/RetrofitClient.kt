package edu.cit.fernandez.goatedmeals.mobile.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.1.17:8080/api/v1/"

    fun getInstance(context: Context): AuthApiService {

        // 1. Create the Client with the Interceptor attached
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        // 2. Build Retrofit using that Client
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach the client here!
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AuthApiService::class.java)
    }
}
