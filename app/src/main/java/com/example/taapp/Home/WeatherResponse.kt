package com.example.taapp.Home

data class WeatherResponse(
    val lokasi: Lokasi,
    val data: List<BMKGData>
)

data class Lokasi(
    val adm1: String,       // digunakan untuk suhu
    val adm2: String,
    val adm3: String,
    val adm4: String,
    val provinsi: String,
    val kotkab: String,
    val kecamatan: String,
    val desa: String,
    val lon: Double,
    val lat: Double,
    val timezone: String,
    val type: String
)

data class BMKGData(
    val cuaca: List<List<CuacaData>>
)

data class CuacaData(
    val datetime: String,
    val local_datetime: String,  // Tambahkan ini
    val t: Int,
    val weather_desc: String,
    val hu: Int,
    val image: String
)
