package ru.aasmc.weather.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import ru.aasmc.weather.data.model.LocationModel

class SharedPreferencesHelper private constructor() {
    companion object {
        private const val WEATHER_PREF_TIME = "Weather pref time"
        private const val WEATHER_FORECAST_PREF_TIME = "Forecast pref time"
        private const val CITY_ID = "City ID"
        private var prefs: SharedPreferences? = null
        private val preferences: SharedPreferences
            get() = prefs!!
        private const val LOCATION = "LOCATION"

        @Volatile
        private var instance: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            synchronized(this) {
                val _instance = instance
                if (_instance == null) {
                    prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    instance = _instance
                }
                return SharedPreferencesHelper()
            }
        }
    }

    fun saveTimeOfInitialWeatherFetch(time: Long) {
        preferences.edit(commit = true) {
            putLong(WEATHER_PREF_TIME, time)
        }
    }

    fun getTimeOfInitialWeatherFetch() = preferences.getLong(WEATHER_PREF_TIME, 0L)

    fun saveTimeOfInitialWeatherForecastFetch(time: Long) {
        preferences.edit(commit = true) {
            putLong(WEATHER_FORECAST_PREF_TIME, time)
        }
    }

    fun getTimeOfInitialWeatherForecastFetch() =
        preferences.getLong(WEATHER_FORECAST_PREF_TIME, 0L)

    fun saveCityId(cityId: Int) {
        preferences.edit(commit = true) {
            putInt(CITY_ID, cityId)
        }
    }

    fun getCityId() = preferences.getInt(CITY_ID, 0)

    fun getUserSetCacheDuration() = preferences.getString("cache_key", "0")

    fun getSelectedThemePref() = preferences.getString("theme_key", "")

    fun getSelectedTemperatureUnit() = preferences.getString("unit_key", "")

    fun saveLocation(location: LocationModel) {
        preferences.edit(commit = true) {
            val gson = Gson()
            val json = gson.toJson(location)
            putString(LOCATION, json)
        }
    }

    fun getLocation(): LocationModel {
        val gson = Gson()
        val json = preferences.getString(LOCATION, null)
        return gson.fromJson(json, LocationModel::class.java)
    }
}