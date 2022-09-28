package ru.aasmc.weather.domain.model

data class Weather(
    val uId: Int,
    val cityId: Int,
    val name: String,
    val wind: Wind,
    val networkWeatherDescription: List<WeatherDescription>,
    val networkWeatherCondition: WeatherCondition
)
