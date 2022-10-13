package ru.aasmc.weather.ui.forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.databinding.WeatherItemBinding
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.util.setTemperature

class WeatherForecastAdapter(
    private val clickListener: ForecastOnClickListener,
    private val weatherPrefs: WeatherPreferences
) : ListAdapter<Forecast, WeatherForecastAdapter.ViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, weatherPrefs)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherForecast = getItem(position)
        holder.bind(weatherForecast)
        holder.itemView.setOnClickListener {
            clickListener.onClick()
        }
    }

    class ViewHolder(
        private val binding: WeatherItemBinding,
        private val weatherPrefs: WeatherPreferences
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherForecast: Forecast) {
            binding.weatherForecast = weatherForecast
            binding.cityTemp.setTemperature(
                weatherForecast.weatherCondition.temp,
                weatherPrefs.temperatureUnit
            )
            val weatherDescription =
                weatherForecast.weatherDescriptions.first()
            binding.weatherForecastDescription = weatherDescription
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, weatherPrefs: WeatherPreferences): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = WeatherItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding, weatherPrefs)
            }
        }
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<Forecast>() {
        override fun areItemsTheSame(
            oldItem: Forecast,
            newItem: Forecast
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Forecast,
            newItem: Forecast
        ): Boolean {
            return oldItem == newItem
        }

    }

    interface ForecastOnClickListener {
        fun onClick() {}
    }
}