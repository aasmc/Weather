package ru.aasmc.weather.domain.model

data class Forecast(
    val id: Int,
    val date: String,
    val wind: Wind,
    val weatherDescriptions: List<WeatherDescription>,
    val weatherCondition: WeatherCondition
)