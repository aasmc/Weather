package ru.aasmc.weather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.aasmc.weather.data.local.dao.WeatherDao
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.typeconverter.ListNetworkWeatherDescriptionConverter

@Database(
    entities = [WeatherDB::class, ForecastDB::class],
    version = 12,
    exportSchema = true
)
@TypeConverters(
    ListNetworkWeatherDescriptionConverter::class
)
abstract class WeatherDatabase: RoomDatabase() {
    abstract val weatherDao: WeatherDao
}