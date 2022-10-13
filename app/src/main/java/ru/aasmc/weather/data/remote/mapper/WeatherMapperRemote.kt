package ru.aasmc.weather.data.remote.mapper

import ru.aasmc.weather.data.remote.model.NetworkWeather
import ru.aasmc.weather.data.remote.model.NetworkWeatherCondition
import ru.aasmc.weather.data.remote.model.NetworkWeatherDescription
import ru.aasmc.weather.data.remote.model.NetworkWind
import ru.aasmc.weather.domain.model.Mapper
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.domain.model.WeatherDescription
import ru.aasmc.weather.domain.model.Wind

class WeatherMapperRemote : Mapper<Weather, NetworkWeather> {

    override fun mapToDomain(type: NetworkWeather): Weather {
        return Weather(
            uId = type.uId,
            cityId = type.cityId,
            name = type.name,
            wind = Wind(
                speed = type.wind.speed,
                deg = type.wind.deg
            ),
            weatherDescriptions = mapDescriptionsToDomain(type.networkWeatherDescriptions),
            weatherCondition = WeatherCondition(
                temp = type.networkWeatherCondition.temp,
                pressure = type.networkWeatherCondition.pressure,
                humidity = type.networkWeatherCondition.humidity
            )
        )
    }

    private fun mapDescriptionsToDomain(
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

    override fun mapFromDomain(type: Weather): NetworkWeather {
        return NetworkWeather(
            uId = type.uId,
            cityId = type.cityId,
            name = type.name,
            wind = NetworkWind(
                speed = type.wind.speed,
                deg = type.wind.deg
            ),
            networkWeatherDescriptions = mapDescriptionsToNetwork(type.weatherDescriptions),
            networkWeatherCondition = NetworkWeatherCondition(
                temp = type.weatherCondition.temp,
                pressure = type.weatherCondition.pressure,
                humidity = type.weatherCondition.humidity
            )
        )
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