package ru.aasmc.weather.data.local.entity

data class WeatherDescriptionDB(
    val id: Long,
    val main: String?,
    val description: String?,
    val icon: String?
)
