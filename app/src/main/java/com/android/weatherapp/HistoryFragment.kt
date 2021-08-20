package com.android.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.android.weatherapp.adapter.WeatherAdapter
import com.android.weatherapp.database.Weather
import com.android.weatherapp.database.WeatherDatabase
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Room.databaseBuilder(
            requireContext().applicationContext,
            WeatherDatabase::class.java,
            "weather_database"
        ).allowMainThreadQueries().build()
        val weatherData = database.weatherDao().getWeatherData()
        weather_history.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = WeatherAdapter(weatherData as MutableList<Weather>)
        }

        if (isLogged) {
            weather_history.visibility = View.VISIBLE
            isLogged = false
        } else {
            weather_history.visibility = View.INVISIBLE
        }

    }

}