package ru.aasmc.weather.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.aasmc.weather.data.source.repository.WeatherRepository

class MyWorkerFactory(
    private val repository: WeatherRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UpdateWeatherWorker::class.java.name -> {
                UpdateWeatherWorker(appContext, workerParameters, repository)
            }
            else -> {
                // return null, so that the base class can delegate to the default WorkerFactory
                null
            }
        }
    }
}