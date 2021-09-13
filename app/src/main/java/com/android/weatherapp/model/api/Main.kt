package com.android.weatherapp.model.api

data class Main(
//    Temperature: Main
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)