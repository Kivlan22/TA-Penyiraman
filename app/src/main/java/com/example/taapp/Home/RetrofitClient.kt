package com.example.tugas2_mobile_kivlanhakeemarrouf_1103213073_tk4506

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // API untuk Sensor 1
    private const val BASE_URL_SENSOR1 = "https://script.google.com/macros/s/AKfycbzlLYObKYt1k8HCPansgMKzxkZ5g0PDkhQidKlWHvtWORjIioRzRtruJ7qdklKgG4Dd/"

    // API untuk Sensor 2
    private const val BASE_URL_SENSOR2 = "https://script.google.com/macros/s/AKfycbybfkf_m4WsQffb3BOIAU7ELnlRdkTbOuJ_DpMrVAv-gTCAGKgGOjCma4mm7eSxhb41/"

    // API untuk Avg Sensor 3
    private const val BASE_URL_SENSOR3 = "https://script.google.com/macros/s/AKfycbyPaSSlTpuKq1IREErSkp2kIOV_2Z0Z_RWrpPcthjQTEH3Y7W9AQbO-u_DdlOMN5jON/"

    // Retrofit untuk Sensor 1
    private val retrofitSensor1: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_SENSOR1)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit untuk Sensor 2
    private val retrofitSensor2: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_SENSOR2)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit untuk Sensor 3
    private val retrofitSensor3: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_SENSOR3)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // ApiService untuk Sensor 1
    val apiServiceSensor1: ApiService = retrofitSensor1.create(ApiService::class.java)

    // ApiService untuk Sensor 2
    val apiServiceSensor2: ApiService = retrofitSensor2.create(ApiService::class.java)

    // ApiService untuk Avg Sensor 3
    val apiServiceSensor3: ApiService = retrofitSensor3.create(ApiService::class.java)
}
