package com.android.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.android.weatherapp.api.WeatherApi
import com.android.weatherapp.database.Weather
import com.android.weatherapp.database.WeatherDatabase
import kotlinx.android.synthetic.main.fragment_current.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://api.openweathermap.org/"
val TAG = "CurrentFragment"

class CurrentFragment : Fragment() {

    var convertFahrenheit: Double = 0.0
    var convertDecimal: Double = 0.0
    lateinit var itemTemperature: String
    lateinit var itemLocation: String
    lateinit var itemSunset: String


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



        getCurrentData()


    }

    private fun getCurrentData() {

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getWeatherData().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.name)

                    withContext(Dispatchers.Main) {
                        tv_location_country.text = data.name.plus(", " + data.sys.country)
                        tv_temperature_value.text = data.main.temp.toString().plus(" °C")
                        tv_sunrise.text = getDate(data.sys.sunrise, "hh:mm a")
                        tv_sunset.text = getDate(data.sys.sunset, "hh:mm a")

                        Log.d(TAG, data.weather[0].description)

                        // Time in 24 hours
                        val rightNow = Calendar.getInstance()
                        val currentHourIn24Format = rightNow[Calendar.HOUR_OF_DAY]

                        if (data.weather[0].description.contains("rain")) {
                            iv_weather.setImageResource(R.drawable.ic_rain)
                        } else if (currentHourIn24Format >= 18) {
                            iv_weather.setImageResource(R.drawable.ic_moon)
                        } else {
                            iv_weather.setImageResource(R.drawable.ic_sun)
                        }

                        convertFahrenheit = (data.main.temp * 1.8 + 32)
                        convertDecimal = String.format("%.3f", convertFahrenheit).toDouble()
                        itemTemperature = convertDecimal.toString().plus(" °F")
                        itemLocation = data.name.plus(", " + data.sys.country)
                        itemSunset = getDate(data.sys.sunset, "hh:mm a")

                        // Add data to Room DB
                        val database = Room.databaseBuilder(
                            requireContext().applicationContext,
                            WeatherDatabase::class.java,
                            "weather_database"
                        ).allowMainThreadQueries().build()
                        if (isLogged) {
                            database.weatherDao().insertWeatherData(
                                Weather(
                                    temperature = itemTemperature,
                                    location = itemLocation,
                                    sunset = itemSunset
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error occurred...", Toast.LENGTH_SHORT).show()
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

}