package ru.aasmc.weather.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast_description",
)
data class ForecastDescriptionDB(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "forecast_id")
    var forecastId: Int,

    @ColumnInfo(name = "main")
    val main: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "icon")
    val icon: String?
)
