package ru.aasmc.weather.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.aasmc.weather.data.source.local.dao.WeatherDao
import ru.aasmc.weather.data.source.local.entity.DBWeather
import ru.aasmc.weather.data.source.local.entity.DBWeatherForecast
import ru.aasmc.weather.util.typeconverters.ListNetworkWeatherDescriptionConverter

@Database(
    entities = [DBWeather::class, DBWeatherForecast::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    ListNetworkWeatherDescriptionConverter::class
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}