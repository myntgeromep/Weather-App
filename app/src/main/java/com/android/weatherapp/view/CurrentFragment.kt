package com.android.weatherapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.android.weatherapp.R
import com.android.weatherapp.model.Login.Companion.isLogged
import com.android.weatherapp.model.WeatherApi
import com.android.weatherapp.model.WeatherData.Companion.convertDecimal
import com.android.weatherapp.model.WeatherData.Companion.convertFahrenheit
import com.android.weatherapp.model.WeatherData.Companion.itemLocation
import com.android.weatherapp.model.WeatherData.Companion.itemSunset
import com.android.weatherapp.model.WeatherData.Companion.itemTemperature
import com.android.weatherapp.model.WeatherData.Companion.weatherDescription
import com.android.weatherapp.model.database.Weather
import com.android.weatherapp.model.database.WeatherDatabase
import com.android.weatherapp.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class CurrentFragment : Fragment() {

    lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        getWeatherApi()
        weatherLiveData()
    }

    private fun getWeatherApi() {

        val api = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getWeatherData().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d("CurrentFragment", response.body()!!.toString())
                    withContext(Dispatchers.Main) {
                        weatherViewModel.currentLocation.value = data.name.plus(", " + data.sys.country)
                        weatherViewModel.currentTemperature.value = data.main.temp.toString()
                        weatherViewModel.currentSunriseTime.value = getDate(data.sys.sunrise, "hh:mm a")
                        weatherViewModel.currentSunsetTime.value = getDate(data.sys.sunset, "hh:mm a")
                        weatherDescription = data.weather[0].description
                        getWeatherUpdate()
                        insertDataRoomDB()
                    }
                }
            } catch (e: UnknownHostException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    Log.d("CurrentFragment", e.toString())
                }
            }
        }
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds * 1000
        return formatter.format(calendar.time)
    }

    fun getWeatherUpdate() {
        val timeNow = Calendar.getInstance()
        val currentHourIn24Format = timeNow[Calendar.HOUR_OF_DAY]
        if (weatherDescription.contains("rain")) {
            iv_weather.setImageResource(R.drawable.ic_rain)
        } else if (currentHourIn24Format >= 18) {
            iv_weather.setImageResource(R.drawable.ic_moon)
        } else {
            iv_weather.setImageResource(R.drawable.ic_sun)
        }
    }

    fun weatherLiveData() {
        weatherViewModel.currentLocation.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_location_country.text = it
            itemLocation = it
        })

        weatherViewModel.currentTemperature.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_temperature_value.text = it.plus(" °C")
            convertTemperatureFahrenheit(it)
        })

        weatherViewModel.currentSunriseTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_sunrise.text = it
        })

        weatherViewModel.currentSunsetTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            tv_sunset.text = it
            itemSunset = it
        })
    }

    fun convertTemperatureFahrenheit(it: String) {
        convertFahrenheit = it.toDouble() * 1.8 + 32
        convertDecimal = String.format("%.3f", convertFahrenheit).toDouble()
        itemTemperature = convertDecimal.toString().plus(" °F")
    }

    fun insertDataRoomDB() {
        val database = Room.databaseBuilder(requireContext().applicationContext, WeatherDatabase::class.java, "weather_database").allowMainThreadQueries().build()
        if (isLogged) {
            database.weatherDao().insertWeatherData(
                Weather(
                    temperature = itemTemperature,
                    location = itemLocation,
                    sunset = itemSunset
                )
            )
        } else {
            Toast.makeText(context, "Data is not added", Toast.LENGTH_SHORT).show()
        }
    }

}