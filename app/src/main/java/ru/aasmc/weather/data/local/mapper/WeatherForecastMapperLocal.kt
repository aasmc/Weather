package ru.aasmc.weather.data.local.mapper

import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.WeatherConditionDB
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import ru.aasmc.weather.data.local.entity.WindDB
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.Mapper
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.domain.model.WeatherDescription
import ru.aasmc.weather.domain.model.Wind

class WeatherForecastMapperLocal : Mapper<List<Forecast>, List<ForecastDB>> {
    override fun mapToDomain(type: List<ForecastDB>): List<Forecast> {
        return type.map { dbForecast ->
            Forecast(
                id = dbForecast.id,
                date = dbForecast.date,
                wind = Wind(
                    speed = dbForecast.wind.speed,
                    deg = dbForecast.wind.deg
                ),
                weatherDescriptions = mapForecastDescriptionsToDomain(dbForecast.networkDescriptions),
                weatherCondition = WeatherCondition(
                    temp = dbForecast.networkWeatherCondition.temp,
                    pressure = dbForecast.networkWeatherCondition.pressure,
                    humidity = dbForecast.networkWeatherCondition.humidity
                )
            )
        }
    }

    private fun mapForecastDescriptionsToDomain(
        descriptions: List<WeatherDescriptionDB>
    ): List<WeatherDescription> {
        return descriptions.map {
            WeatherDescription(
                id = it.id,
                main = it.main,
                description = it.description,
                icon = it.icon
            )
        }
    }

    override fun mapFromDomain(type: List<Forecast>): List<ForecastDB> {
        return type.map { forecast ->
            ForecastDB(
                    date = forecast.date,
                    wind = WindDB(
                        speed = forecast.wind.speed,
                        deg = forecast.wind.deg
                    ),
                    networkWeatherCondition = WeatherConditionDB(
                        temp = forecast.weatherCondition.temp,
                        pressure = forecast.weatherCondition.pressure,
                        humidity = forecast.weatherCondition.humidity
                    ),
                    networkDescriptions = mapDescriptionsToDb(forecast.weatherDescriptions)
                )
        }
    }

    private fun mapDescriptionsToDb(
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