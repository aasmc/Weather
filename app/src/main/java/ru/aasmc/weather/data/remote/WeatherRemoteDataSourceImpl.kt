package ru.aasmc.weather.data.remote

import android.content.Context
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.aasmc.weather.R
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.data.remote.model.NetworkWeather
import ru.aasmc.weather.data.remote.model.NetworkWeatherForecast
import ru.aasmc.weather.di.scope.IoDispatcher
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.util.Result
import ru.aasmc.weather.util.convertKelvinToCelsius
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val apiService: WeatherApiService,
    private val context: Context,
    private val weatherPreferences: WeatherPreferences
) : WeatherRemoteDataSource {

    override suspend fun getWeather(location: LocationModel): Result<NetworkWeather> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getCurrentWeather(
                    location.latitude, location.longitude
                )
                if (result.isSuccessful) {
                    val networkWeather = result.body()
                    Result.Success(networkWeather)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }

    override suspend fun getWeatherForecast(cityId: Int): Result<List<NetworkWeatherForecast>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getWeatherForecast(cityId)
                if (result.isSuccessful) {
                    val networkWeatherForecast = result.body()?.weathers
                    Result.Success(networkWeatherForecast)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }

    override suspend fun getSearchWeather(query: String): Result<NetworkWeather> =
        withContext(ioDispatcher) {
            return@withContext try {
                val result = apiService.getSpecificWeather(query)
                if (result.isSuccessful) {
                    val networkWeather = result.body()
                    if (networkWeather != null) {
                        networkWeather.networkWeatherCondition.temp =
                            if (weatherPreferences.temperatureUnit == context.getString(
                                    R.string.temp_unit_fahrenheit
                                )
                            ) {
                                networkWeather.networkWeatherCondition.temp
                            } else {
                                convertKelvinToCelsius(networkWeather.networkWeatherCondition.temp)
                            }
                        Result.Success(networkWeather)
                    } else {
                        Result.Success(null)
                    }
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }
}