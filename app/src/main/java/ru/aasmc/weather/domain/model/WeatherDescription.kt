package ru.aasmc.weather.domain.model

data class WeatherDescription(
    val id: Long,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)
