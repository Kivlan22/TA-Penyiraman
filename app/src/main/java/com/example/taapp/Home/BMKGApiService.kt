package com.example.taapp.Home

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BMKGApiService {
    @GET("publik/prakiraan-cuaca")
    fun getWeatherForecast(
        @Query("adm4") adm4: String
    ): Call<WeatherResponse>

    companion object {
        private const val BASE_URL = "https://api.bmkg.go.id/"

        val instance: BMKGApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BMKGApiService::class.java)
        }
    }
}
