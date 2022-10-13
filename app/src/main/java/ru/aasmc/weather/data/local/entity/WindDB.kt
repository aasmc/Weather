package ru.aasmc.weather.data.local.entity

import androidx.room.ColumnInfo

data class WindDB(
    @ColumnInfo(name = "speed")
    val speed: Double,
    @ColumnInfo(name = "degree")
    val deg: Int
)
