package ru.aasmc.weather.data.remote.mapper

import ru.aasmc.weather.data.remote.model.NetworkWeatherCondition
import ru.aasmc.weather.data.remote.model.NetworkWeatherDescription
import ru.aasmc.weather.data.remote.model.NetworkWeatherForecast
import ru.aasmc.weather.data.remote.model.NetworkWind
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.Mapper
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.domain.model.WeatherDescription
import ru.aasmc.weather.domain.model.Wind

class WeatherForecastMapperRemote : Mapper<List<Forecast>, List<NetworkWeatherForecast>> {

    override fun mapToDomain(type: List<NetworkWeatherForecast>): List<Forecast> {
        return type.map {
            Forecast(
                id = it.id,
                date = it.date,
                wind = Wind(
                    speed = it.wind.speed,
                    deg = it.wind.deg
                ),
                weatherDescriptions = mapNetworkDescriptionsToDomain(it.networkWeatherDescription),
                weatherCondition = WeatherCondition(
                    temp = it.networkWeatherCondition.temp,
                    pressure = it.networkWeatherCondition.pressure,
                    humidity = it.networkWeatherCondition.humidity
                )
            )
        }
    }

    private fun mapNetworkDescriptionsToDomain(
        descriptions: List<NetworkWeatherDescription>
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

    override fun mapFromDomain(type: List<Forecast>): List<NetworkWeatherForecast> {
        return type.map {
            NetworkWeatherForecast(
                id = it.id,
                date = it.date,
                wind = NetworkWind(
                    speed = it.wind.speed,
                    deg = it.wind.deg
                ),
                networkWeatherDescription = mapDescriptionsToNetwork(it.weatherDescriptions),
                networkWeatherCondition = NetworkWeatherCondition(
                    temp = it.weatherCondition.temp,
                    pressure = it.weatherCondition.pressure,
                    humidity = it.weatherCondition.humidity
                )
            )
        }
    }

    private fun mapDescriptionsToNetwork(
        descriptions: List<WeatherDescription>
    ): List<NetworkWeatherDescription> {
        return descriptions.map {
            NetworkWeatherDescription(
                id = it.id,
                main = it.main,
                description = it.description,
                icon = it.icon
            )
        }
    }

}