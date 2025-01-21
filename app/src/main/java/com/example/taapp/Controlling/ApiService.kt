package com.example.taapp.Controlling

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    // API untuk mendapatkan nilai kelembapan
    @GET("AKfycbwqhHBaR0F6ZPDr8ogRo4jsnamiUwhz-vnn-DkeB2L58hM48YGYUWwcyZIRdqBPP9qQ/exec")
    fun getAvgSensor(): Call<SensorResponse>

    // Trigger API untuk mengaktifkan otomatis
    @GET("AKfycbyS4T7FLXGG5u8UvEkn20M9ivnRZV0Mi8kiRY5cxlsozdIleFal2cK06knPWW8nvGhHqQ/exec")
    fun setOtomatisOn(): Call<Unit>

    // Trigger API untuk menonaktifkan otomatis
    @GET("AKfycbwszi0N1H6j77f-JGevV0dtr0oUEyJLFnjA-aZ9bQrVd7T_SZ0WgcIWoHk9LR4Wyev6Pg/exec")
    fun setOtomatisOff(): Call<Unit>

    // Trigger API untuk mengaktifkan manual
    @GET("AKfycbxBg5b7mO_0YFZd97-Se8nnkKVh8CMzmfRalWVJ9y2btqurAO4JKsWqqrpmeCo45vWh/exec")
    fun setManualOn(): Call<Unit>

    // Trigger API untuk menonaktifkan manual
    @GET("AKfycbzJnJq68Ic_Jm7b_lQbxSx24NHIqfDdzL1Bt1QHlckQ47QiFHM2JEijRSFIBfy1Ir-c/exec")
    fun setManualOff(): Call<Unit>
}
