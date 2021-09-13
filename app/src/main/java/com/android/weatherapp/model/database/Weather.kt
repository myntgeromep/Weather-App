package com.android.weatherapp.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Weather(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,

    val temperature: String,
    val location: String,
    val sunset: String
)