package ru.aasmc.weather.data.source.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.aasmc.weather.data.model.LocationModel
import ru.aasmc.weather.data.model.NetworkWeather
import ru.aasmc.weather.data.model.NetworkWeatherForecast
import ru.aasmc.weather.data.source.remote.retrofit.WeatherApiService
import ru.aasmc.weather.di.scope.IoDispatcher
import ru.aasmc.weather.util.Result
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val apiService: WeatherApiService
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
                    Result.Success(networkWeather)
                } else {
                    Result.Success(null)
                }
            } catch (exception: Exception) {
                Result.Error(exception)
            }
        }
}