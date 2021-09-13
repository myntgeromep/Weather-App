package com.android.weatherapp.model.api

data class WeatherJson(
//    Location: WeatherJson
    val base: String,
    val cod: Int,
    val dt: Long,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>
)