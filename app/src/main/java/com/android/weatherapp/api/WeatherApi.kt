package com.android.weatherapp.api

import retrofit2.Call

interface WeatherApi {

//    @GET("data/2.5/weather?q=Manila&appid=<--PASTE HERE-->&units=metric")
    fun getWeatherData(): Call<WeatherJson>

}