package ru.aasmc.weather.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.github.pwittchen.weathericonview.WeatherIconView
import ru.aasmc.weather.R

@BindingAdapter("setIcon")
fun WeatherIconView.showIcon(condition: String?) {
    val context = this.context
    WeatherIconGenerator.getIconResources(context, this, condition)
}

@BindingAdapter("setTemperature")
fun TextView.setTemperature(double: Double) {
    val context = this.context
    if (SharedPreferencesHelper.getInstance(context).getSelectedTemperatureUnit()
        == context.getString(R.string.temp_unit_fahrenheit)) {
        this.text = "${double}${context.resources.getString(R.string.temp_symbol_fahrenheit)}"
    } else {
        this.text = "$double${context.resources.getString(R.string.temp_symbol_celsius)}"
    }
}