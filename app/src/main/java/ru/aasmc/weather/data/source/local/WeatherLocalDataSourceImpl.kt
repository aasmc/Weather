package ru.aasmc.weather.data.source.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.aasmc.weather.data.source.local.dao.WeatherDao
import ru.aasmc.weather.data.source.local.entity.DBWeather
import ru.aasmc.weather.data.source.local.entity.DBWeatherForecast
import ru.aasmc.weather.di.scope.IoDispatcher
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : WeatherLocalDataSource {
    override suspend fun getWeather(): DBWeather? = withContext(ioDispatcher) {
        return@withContext weatherDao.getWeather()
    }

    override suspend fun saveWeather(weather: DBWeather) = withContext(ioDispatcher) {
        weatherDao.insertWeather(weather)
    }

    override suspend fun deleteWeather() = withContext(ioDispatcher) {
        weatherDao.deleteAllWeather()
    }

    override suspend fun getForecastWeather(): List<DBWeatherForecast>? =
        withContext(ioDispatcher) {
            return@withContext weatherDao.getAllWeatherForecast()
        }

    override suspend fun saveForecastWeather(weatherForecast: DBWeatherForecast) =
        withContext(ioDispatcher) {
            weatherDao.insertForecastWeather(weatherForecast)
        }

    override suspend fun deleteForecastWeather() = withContext(ioDispatcher) {
        weatherDao.deleteAllWeatherForecast()
    }
}