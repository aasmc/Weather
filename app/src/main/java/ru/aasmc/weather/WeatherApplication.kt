package ru.aasmc.weather

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.aasmc.weather.data.preferences.WeatherPreferences
import ru.aasmc.weather.util.ThemeManager
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var weatherPreferences: WeatherPreferences

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initTheme()
    }

    private fun initTheme() {
        runCatching {
            ThemeManager.applyTheme(weatherPreferences.theme)
        }.onFailure { exception ->
            Timber.e("Theme Manager: $exception")
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }
}