package ru.aasmc.weather.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filter
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.util.NotificationHelper
import javax.inject.Inject

@HiltWorker
class UpdateWeatherWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, params) {
    private val notificationHelper = NotificationHelper("Weather Update", context)

    @Inject
    lateinit var weatherPrefs: WeatherPreferences

    override suspend fun doWork(): Result {
        val location = weatherPrefs.location

        var finalResult: Result? = null

        repository.getWeather(location, true)
            .filter { it !is ru.aasmc.weather.util.Result.Loading }
            .collect { result ->
                when (result) {
                    is ru.aasmc.weather.util.Result.Success -> {
                        if (result.data != null) {
                            repository.getForecast(result.data.cityId, true)
                                .filter { it !is ru.aasmc.weather.util.Result.Loading }
                                .collect { forecastResult ->
                                    finalResult = when (forecastResult) {
                                        is ru.aasmc.weather.util.Result.Success -> {
                                            if (forecastResult.data != null) {
                                                notificationHelper.createNotification()
                                                Result.success()
                                            } else {
                                                Result.failure()
                                            }
                                        }
                                        else -> {
                                            Result.failure()
                                        }
                                    }
                                }
                        }
                    }
                    else -> {
                        finalResult = Result.failure()
                    }
                }
            }
        return finalResult ?: Result.failure()
    }
}