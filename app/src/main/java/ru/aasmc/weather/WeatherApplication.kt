package ru.aasmc.weather

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import ru.aasmc.weather.data.source.repository.WeatherRepository
import ru.aasmc.weather.util.ThemeManager
import ru.aasmc.weather.worker.MyWorkerFactory
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class WeatherApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var weatherRepository: WeatherRepository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initTheme()
    }

    private fun initTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        kotlin.runCatching {
            ThemeManager.applyTheme(requireNotNull(prefs.getString("theme_key", "")))
        }.onFailure { exception ->
            Timber.e("Theme Manager: $exception")
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(MyWorkerFactory(weatherRepository))
        // Add here other factories that you may need in this application
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}