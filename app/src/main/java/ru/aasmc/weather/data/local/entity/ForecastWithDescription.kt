package ru.aasmc.weather.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ForecastWithDescription(
    @Embedded
    val forecastDB: ForecastDB,

    @Relation(
        parentColumn = "id",
        entityColumn = "forecast_id",
    )
    val descriptions: List<ForecastDescriptionDB>
)
