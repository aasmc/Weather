package ru.aasmc.weather.ui.forecast

import com.shrikanthravi.collapsiblecalendarview.data.Day

sealed class ForecastEvent {
    data class UpdateWeatherForecast(val selectedDay: Day, val cityId: Int): ForecastEvent()
    data class ObserveForecast(val cityId: Int): ForecastEvent()
    data class RefreshForecast(val cityId: Int): ForecastEvent()
}
