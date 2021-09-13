package com.android.weatherapp.model.api

data class Sys(
    // Sunset & Sunrise: Sys
    val country: String,
    val id: Int,
    val sunrise: Long,
    val sunset: Long,
    val type: Int
)