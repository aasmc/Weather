package ru.aasmc.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherDB(
    @ColumnInfo(name = "weather_id")
    @PrimaryKey(autoGenerate = true)
    val uId: Int = 0,

    @ColumnInfo(name = "city_id")
    val cityId: Int,

    @ColumnInfo(name = "city_name")
    val cityName: String,

    @Embedded
    val wind: WindDB,

    @ColumnInfo(name = "weather_descriptions")
    val weatherDescriptions: List<WeatherDescriptionDB>,

    @Embedded
    val weatherCondition: WeatherConditionDB
)
