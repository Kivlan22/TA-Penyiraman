package com.example.tugas2_mobile_kivlanhakeemarrouf_1103213073_tk4506

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("exec") // API for Sensor 1
    fun getSensor1Humidity(): Call<Double>

    @GET("exec") // API for Sensor 2
    fun getSensor2Humidity(): Call<Double>

    @GET("exec") // API for Avg Sensor 3
    fun getAvgSensorHumidity(): Call<Double>
}
