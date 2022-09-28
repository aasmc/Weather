package ru.aasmc.weather.domain.model

data class Forecast(
    val id: Int,
    val date: String,
    val wind: Wind,
    val networkWeatherDescription: List<WeatherDescription>,
    val networkWeatherCondition: WeatherCondition
)