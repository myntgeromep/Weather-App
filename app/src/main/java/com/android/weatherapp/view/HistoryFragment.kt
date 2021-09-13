package com.android.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.android.weatherapp.R
import com.android.weatherapp.model.Login.Companion.isLogged
import com.android.weatherapp.model.database.Weather
import com.android.weatherapp.model.database.WeatherDatabase
import com.android.weatherapp.view.adapter.WeatherAdapter
import com.android.weatherapp.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        // Room DB
        val database = Room.databaseBuilder(requireContext().applicationContext, WeatherDatabase::class.java, "weather_database").allowMainThreadQueries().build()

        // RecyclerView
        val weatherData = database.weatherDao().getWeatherData()
        weather_history.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = WeatherAdapter(weatherData as MutableList<Weather>)
        }
        // Show History if user isLogged
        if (isLogged) {
            weather_history.visibility = View.VISIBLE
        } else {
            weather_history.visibility = View.INVISIBLE
        }

    }

}