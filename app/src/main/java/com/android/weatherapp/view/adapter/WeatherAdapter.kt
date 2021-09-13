package com.android.weatherapp.view.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.weatherapp.R
import com.android.weatherapp.model.database.Weather
import com.android.weatherapp.model.database.WeatherDatabase
import kotlinx.android.synthetic.main.item_layout.view.*

class WeatherAdapter(private val weatherData: MutableList<Weather>) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)  {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tv_item_temperature_value.text = weatherData[position].temperature
        holder.view.tv_item_location_value.text = weatherData[position].location
        holder.view.tv_item_sunset_value.text = weatherData[position].sunset

        holder.itemView.setOnClickListener(View.OnClickListener {
            val database = Room.databaseBuilder(it.context, WeatherDatabase::class.java, "weather_database").allowMainThreadQueries().build()

            var builder = AlertDialog.Builder(it.context)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete this item?")

            builder.setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
            builder.setPositiveButton("Yes") { dialog, id ->
                database.weatherDao().deleteWeatherData(weatherData[holder.adapterPosition].uid)
                weatherData.remove(weatherData[holder.adapterPosition])
                notifyDataSetChanged()
                dialog.cancel()
            }
            var alert = builder.create()
            alert.show()

        })

    }

    override fun getItemCount(): Int {
        return weatherData.size
    }


}
