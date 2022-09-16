package ru.aasmc.weather.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.aasmc.weather.data.source.repository.WeatherRepository
import ru.aasmc.weather.util.NotificationHelper
import ru.aasmc.weather.util.SharedPreferencesHelper

class UpdateWeatherWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)
    private val sharedPrefs = SharedPreferencesHelper.getInstance(context)

    override suspend fun doWork(): Result {
        val location = sharedPrefs.getLocation()
        return when (val result = repository.getWeather(location, true)) {
            is ru.aasmc.weather.util.Result.Success -> {
                if (result.data != null) {
                    when (
                        val foreResult =
                            repository.getForecastWeather(result.data.cityId, true)
                    ) {
                        is ru.aasmc.weather.util.Result.Success -> {
                            if (foreResult.data != null) {
                                notificationHelper.createNotification()
                                Result.success()
                            } else {
                                Result.failure()
                            }
                        }
                        else -> Result.failure()
                    }
                } else {
                    Result.failure()
                }
            }
            else -> Result.failure()
        }
    }
}