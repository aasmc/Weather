package ru.aasmc.weather.data.local.source

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.aasmc.weather.R
import ru.aasmc.weather.data.local.dao.WeatherDao
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.WeatherConditionDB
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.di.scope.IoDispatcher
import ru.aasmc.weather.util.convertCelsiusToFahrenheit
import ru.aasmc.weather.util.convertKelvinToCelsius
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val context: Context,
    private val weatherPreferences: WeatherPreferences
) : WeatherLocalDataSource {

    override fun observeWeather(): Flow<WeatherDB> {
        return weatherDao.observeWeather()
            .map { weather ->
                WeatherDB(
                    uId = weather.uId,
                    cityId = weather.cityId,
                    cityName = weather.cityName,
                    wind = weather.wind,
                    weatherDescriptions = weather.weatherDescriptions,
                    weatherCondition = WeatherConditionDB(
                        temp = if (weatherPreferences.temperatureUnit == context.resources.getString(
                                R.string.temp_unit_fahrenheit
                            )
                        ) {
                            convertKelvinToCelsius(weather.weatherCondition.temp)
                        } else {
                            weather.weatherCondition.temp
                        },
                        pressure = weather.weatherCondition.pressure,
                        humidity = weather.weatherCondition.humidity
                    )
                )
            }
    }

    override suspend fun saveWeather(weather: WeatherDB) {

        val w = WeatherDB(
            uId = weather.uId,
            cityId = weather.cityId,
            cityName = weather.cityName,
            wind = weather.wind,
            weatherDescriptions = weather.weatherDescriptions,
            weatherCondition = WeatherConditionDB(
                temp = convertKelvinToCelsius(weather.weatherCondition.temp),
                pressure = weather.weatherCondition.pressure,
                humidity = weather.weatherCondition.humidity
            )
        )
        withContext(ioDispatcher) {
            weatherDao.insertWeather(w)
        }
    }

    override suspend fun deleteWeather() {
        withContext(ioDispatcher) {
            weatherDao.deleteAllWeather()
        }
    }

    override fun observeForecastWeather(): Flow<List<ForecastDB>> {
        return weatherDao.observeAllWeatherForecasts().map { list ->
            list.map { f ->
                ForecastDB(
                    id = f.id,
                    date = f.date,
                    wind = f.wind,
                    networkDescriptions = f.networkDescriptions,
                    networkWeatherCondition = WeatherConditionDB(
                        temp = if (weatherPreferences.temperatureUnit == context.resources.getString(
                                R.string.temp_unit_fahrenheit
                            )
                        ) {
                            convertCelsiusToFahrenheit(f.networkWeatherCondition.temp)
                        } else {
                            f.networkWeatherCondition.temp
                        },
                        pressure = f.networkWeatherCondition.pressure,
                        humidity = f.networkWeatherCondition.humidity
                    )
                )
            }
        }
    }

    override suspend fun saveForecastWeather(weatherForecast: ForecastDB) {
        val f = ForecastDB(
            id = weatherForecast.id,
            date = weatherForecast.date,
            wind = weatherForecast.wind,
            networkDescriptions = weatherForecast.networkDescriptions,
            networkWeatherCondition = WeatherConditionDB(
                temp = convertKelvinToCelsius(weatherForecast.networkWeatherCondition.temp),
                pressure = weatherForecast.networkWeatherCondition.pressure,
                humidity = weatherForecast.networkWeatherCondition.humidity
            )
        )
        withContext(ioDispatcher) {
            weatherDao.insertForecast(f)
        }
    }

    override suspend fun saveAllForecasts(vararg forecasts: ForecastDB) {
        for (fsct in forecasts) {
            saveForecastWeather(fsct)
        }
    }

    override suspend fun deleteForecastWeather() {
        withContext(ioDispatcher) {
            weatherDao.deleteAllWeatherForecast()
        }
    }

    override suspend fun getAllForecasts(): List<ForecastDB> {
        return weatherDao.getAllForecasts().map { f ->
            ForecastDB(
                id = f.id,
                date = f.date,
                wind = f.wind,
                networkDescriptions = f.networkDescriptions,
                networkWeatherCondition = WeatherConditionDB(
                    temp = if (weatherPreferences.temperatureUnit == context.resources.getString(
                            R.string.temp_unit_fahrenheit
                        )
                    ) {
                        convertCelsiusToFahrenheit(f.networkWeatherCondition.temp)
                    } else {
                        f.networkWeatherCondition.temp
                    },
                    pressure = f.networkWeatherCondition.pressure,
                    humidity = f.networkWeatherCondition.humidity
                )
            )
        }
    }

}