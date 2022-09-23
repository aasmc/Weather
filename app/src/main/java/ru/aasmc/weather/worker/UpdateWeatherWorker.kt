package ru.aasmc.weather.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.data.source.repository.WeatherRepository
import ru.aasmc.weather.util.NotificationHelper
import javax.inject.Inject

@HiltWorker
class UpdateWeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WeatherRepository
) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)

    @Inject
    lateinit var weatherPrefs: WeatherPreferences

    override suspend fun doWork(): Result {
        val location = weatherPrefs.location
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