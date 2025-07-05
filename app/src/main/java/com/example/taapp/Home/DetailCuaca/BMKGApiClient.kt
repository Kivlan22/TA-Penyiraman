package com.example.taapp.Home.DetailCuaca

import com.example.taapp.Home.BMKGApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BMKGApiClient {
    private const val BASE_URL = "https://api.bmkg.go.id/"

    val apiService: BMKGApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BMKGApiService::class.java)
    }
}
