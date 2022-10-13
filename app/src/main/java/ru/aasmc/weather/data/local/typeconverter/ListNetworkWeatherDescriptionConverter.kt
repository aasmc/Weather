package ru.aasmc.weather.data.local.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import java.lang.reflect.Type

class ListNetworkWeatherDescriptionConverter {
    val gson = Gson()
    val type: Type = object : TypeToken<List<WeatherDescriptionDB>>() {}.type

    @TypeConverter
    fun fromWeatherListToString(list: List<WeatherDescriptionDB>): String {
        return gson.toJson(list, type)
    }

    @TypeConverter
    fun toWeatherFromString(json: String): List<WeatherDescriptionDB> {
        return gson.fromJson(json, type)
    }
}