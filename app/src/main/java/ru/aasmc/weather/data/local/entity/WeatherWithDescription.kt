package ru.aasmc.weather.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WeatherWithDescription(
    @Embedded
    val weatherDB: WeatherDB,
    @Relation(
        parentColumn = "weather_id",
        entityColumn = "weather_id"
    )
    val descriptions: List<WeatherDescriptionDB>
)
