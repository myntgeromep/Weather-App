package com.android.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel: ViewModel() {

    val currentLocation: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentTemperature: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentSunriseTime: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentSunsetTime: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


}