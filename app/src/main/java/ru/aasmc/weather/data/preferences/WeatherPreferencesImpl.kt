package ru.aasmc.weather.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.aasmc.weather.R
import ru.aasmc.weather.domain.model.LocationModel
import javax.inject.Inject

class WeatherPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : WeatherPreferences {

    private val defaultThemeValue = context.getString(R.string.follow_system_value)

    private val preferenceKeyChangedFlow =
        MutableSharedFlow<String>(extraBufferCapacity = 1)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        preferenceKeyChangedFlow.tryEmit(key)
    }

    override fun setup() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override var theme: String
        get() = sharedPreferences.getString(THEME_KEY, defaultThemeValue)!!
        set(value) = sharedPreferences.edit {
            putString(THEME_KEY, value)
        }

    override fun observeTheme(): Flow<String> {
        return preferenceKeyChangedFlow
            .onStart { emit(THEME_KEY) }
            .filter { it == THEME_KEY }
            .map { theme }
            .distinctUntilChanged()
    }

    override var cityId: Int
        get() = sharedPreferences.getInt(CITY_ID_KEY, 0)
        set(value) = sharedPreferences.edit {
            putInt(CITY_ID_KEY, value)
        }

    override fun observeCityId(): Flow<Int> {
        return preferenceKeyChangedFlow
            .onStart { emit(CITY_ID_KEY) }
            .filter { it == CITY_ID_KEY }
            .map { cityId }
            .distinctUntilChanged()
    }

    override var cacheDuration: String
        get() = sharedPreferences.getString(CACHE_KEY, "0")!!
        set(value) = sharedPreferences.edit {
            putString(CACHE_KEY, value)
        }

    override fun observeCacheDuration(): Flow<String> {
        return preferenceKeyChangedFlow
            .onStart { emit(CACHE_KEY) }
            .filter { it == CACHE_KEY }
            .map { cacheDuration }
            .distinctUntilChanged()
    }

    override var temperatureUnit: String
        get() = sharedPreferences.getString(UNIT_KEY, "")!!
        set(value) = sharedPreferences.edit {
            putString(UNIT_KEY, value)
        }

    override fun observeTemperatureUnit(): Flow<String> {
        return preferenceKeyChangedFlow
            .onStart { emit(UNIT_KEY) }
            .filter { it == UNIT_KEY }
            .map { temperatureUnit }
            .distinctUntilChanged()
    }

    override var location: LocationModel
        get() {
            val gson = Gson()
            val json = sharedPreferences.getString(LOCATION_KEY, null)
            return gson.fromJson(json, LocationModel::class.java)
        }
        set(value) = sharedPreferences.edit {
            val gson = Gson()
            val str = gson.toJson(value)
            putString(LOCATION_KEY, str)
        }

    override fun observeLocation(): Flow<LocationModel> {
        return preferenceKeyChangedFlow
            .onStart { emit(LOCATION_KEY) }
            .filter { it == LOCATION_KEY }
            .map { location }
            .distinctUntilChanged()
    }

    override var timeOfInitialWeatherFetch: Long
        get() = sharedPreferences.getLong(WEATHER_FETCH_KEY, 0)
        set(value) = sharedPreferences.edit {
            putLong(WEATHER_FETCH_KEY, value)
        }

    override fun observeTimeOfInitialWeatherFetch(): Flow<Long> {
        return preferenceKeyChangedFlow
            .onStart { emit(WEATHER_FETCH_KEY) }
            .filter { it == WEATHER_FETCH_KEY }
            .map { timeOfInitialWeatherFetch }
            .distinctUntilChanged()
    }

    override var timeOfInitialWeatherForecastFetch: Long
        get() = sharedPreferences.getLong(WEATHER_FORECAST_FETCH_KEY, 0)
        set(value) = sharedPreferences.edit {
            putLong(WEATHER_FORECAST_FETCH_KEY, value)
        }

    override fun observeTimeOfInitialWeatherForecastFetch(): Flow<Long> {
        return preferenceKeyChangedFlow
            .onStart { emit(WEATHER_FORECAST_FETCH_KEY) }
            .filter { it == WEATHER_FORECAST_FETCH_KEY }
            .map { timeOfInitialWeatherForecastFetch }
            .distinctUntilChanged()
    }

    companion object {
        private const val THEME_KEY = "theme_key"
        private const val CACHE_KEY = "cache_key"
        private const val UNIT_KEY = "unit_key"
        private const val CITY_ID_KEY = "city_id_key"
        private const val WEATHER_FETCH_KEY = "Weather pref time"
        private const val WEATHER_FORECAST_FETCH_KEY = "Forecast pref time"
        private const val LOCATION_KEY = "LOCATION"
    }
}