package ru.aasmc.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast")
data class ForecastDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @Embedded
    val wind: WindDB,

    @ColumnInfo(name = "weather_description")
    val networkDescriptions: List<WeatherDescriptionDB>,

    @Embedded
    val networkWeatherCondition: WeatherConditionDB
)
