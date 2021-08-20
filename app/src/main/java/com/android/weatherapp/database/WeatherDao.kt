package com.android.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather")
    fun getWeatherData(): List<Weather>

    @Insert
    fun insertWeatherData(weather: Weather)

    @Query("DELETE FROM weather WHERE uid = :id")
    fun deleteWeatherData(id: Int)

}