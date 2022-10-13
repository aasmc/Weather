package ru.aasmc.weather.data.local.mapper

import ru.aasmc.weather.data.local.entity.WeatherConditionDB
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import ru.aasmc.weather.data.local.entity.WindDB
import ru.aasmc.weather.domain.model.Mapper
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.domain.model.WeatherDescription
import ru.aasmc.weather.domain.model.Wind

class WeatherMapperLocal : Mapper<Weather, WeatherDB> {

    override fun mapToDomain(type: WeatherDB): Weather {
        return Weather(
            uId = type.uId,
            cityId = type.cityId,
            wind = Wind(
                speed = type.wind.speed,
                deg = type.wind.deg
            ),
            name = type.cityName,
            weatherDescriptions = mapWeatherDescriptionsToDomain(type.weatherDescriptions),
            weatherCondition = WeatherCondition(
                temp = type.weatherCondition.temp,
                pressure = type.weatherCondition.pressure,
                humidity = type.weatherCondition.humidity
            )
        )
    }

    private fun mapWeatherDescriptionsToDomain(descriptions: List<WeatherDescriptionDB>): List<WeatherDescription> {
        return descriptions.map {
            WeatherDescription(
                id = it.id,
                main = it.main,
                description = it.description,
                icon = it.icon
            )
        }
    }

    override fun mapFromDomain(type: Weather): WeatherDB {
        return WeatherDB(
            cityId = type.cityId,
            cityName = type.name,
            wind = WindDB(
                speed = type.wind.speed,
                deg = type.wind.deg
            ),
            weatherCondition = WeatherConditionDB(
                temp = type.weatherCondition.temp,
                pressure = type.weatherCondition.pressure,
                humidity = type.weatherCondition.humidity
            ),
            weatherDescriptions = mapWeatherDescriptionToDB(
                type.weatherDescriptions
            )
        )
    }

    private fun mapWeatherDescriptionToDB(
        descriptions: List<WeatherDescription>
    ): List<WeatherDescriptionDB> {
        return descriptions.map {
            WeatherDescriptionDB(
                id = it.id,
                main = it.main,
                description = it.description,
                icon = it.icon
            )
        }
    }
}