package ru.aasmc.weather.mapper

import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.data.source.local.entity.DBWeather

class WeatherMapperLocal : BaseMapper<DBWeather, Weather> {
    override fun transformToDomain(type: DBWeather): Weather = Weather(
        uId = type.uId,
        cityId = type.cityId,
        name = type.cityName,
        wind = type.wind,
        networkWeatherDescription = type.networkWeatherDescriptions,
        networkWeatherCondition = type.networkWeatherCondition
    )

    override fun transformToDto(type: Weather): DBWeather = DBWeather(
        uId = type.uId,
        cityId = type.cityId,
        cityName = type.name,
        wind = type.wind,
        networkWeatherDescriptions = type.networkWeatherDescription,
        networkWeatherCondition = type.networkWeatherCondition
    )
}