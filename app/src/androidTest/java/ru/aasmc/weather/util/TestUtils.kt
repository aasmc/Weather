package ru.aasmc.weather.util

import ru.aasmc.weather.data.exceptions.DBException
import ru.aasmc.weather.data.exceptions.NetworkException
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.WeatherConditionDB
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import ru.aasmc.weather.data.local.entity.WindDB
import ru.aasmc.weather.data.remote.model.NetworkWeather
import ru.aasmc.weather.data.remote.model.NetworkWeatherCondition
import ru.aasmc.weather.data.remote.model.NetworkWeatherDescription
import ru.aasmc.weather.data.remote.model.NetworkWeatherForecast
import ru.aasmc.weather.data.remote.model.NetworkWind
import ru.aasmc.weather.domain.model.Forecast
import ru.aasmc.weather.domain.model.LocationModel
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.domain.model.WeatherCondition
import ru.aasmc.weather.domain.model.WeatherDescription
import ru.aasmc.weather.domain.model.Wind

val fakeDbWeatherEntity = WeatherDB(
    1,
    123,
    "Lagos",
    WindDB(32.5, 24),
    listOf(WeatherDescriptionDB(1L, "Main", "Cloudy", "Icon")),
    WeatherConditionDB(324.43, 1234.32, 32.5)
)

val fakeDbWeatherForecast = ForecastDB(
    1, "2021-07-25 14:22:10", WindDB(22.2, 21),
    listOf(
        WeatherDescriptionDB(1L, "Main", "Desc", "Icon")
    ),
    WeatherConditionDB(22.3, 22.2, 22.2)
)

val dummyLocation = LocationModel(12.2, 23.4)

val fakeNetworkWeather = NetworkWeather(
    1,
    123,
    "Lagos",
    NetworkWind(32.5, 24),
    listOf(NetworkWeatherDescription(1L, "Main", "Cloudy", "Icon")),
    NetworkWeatherCondition(324.43, 1234.32, 32.5)
)

val fakeNetworkWeatherForecast = NetworkWeatherForecast(
    1, "Date", NetworkWind(22.2, 21),
    listOf(
        NetworkWeatherDescription(1L, "Main", "Desc", "Icon")
    ),
    NetworkWeatherCondition(22.3, 22.2, 22.2)
)

val fakeWeather = Weather(
    1,
    123,
    "Lagos",
    Wind(32.5, 24),
    listOf(WeatherDescription(1L, "Main", "Cloudy", "Icon")),
    WeatherCondition(324.43, 1234.32, 32.5)
)

val fakeWeatherForecast = Forecast(
    1, "2021-07-25 14:22:10", Wind(22.2, 21),
    listOf(
        WeatherDescription(1L, "Main", "Desc", "Icon")
    ),
    WeatherCondition(22.3, 22.2, 22.2)
)

fun createNewWeatherForecast(date: String): Forecast {
    return Forecast(
        1, date, Wind(22.2, 21),
        listOf(
            WeatherDescription(1L, "Main", "Desc", "Icon")
        ),
        WeatherCondition(22.3, 22.2, 22.2)
    )
}

val fakeWeatherForecastList = listOf(
    createNewWeatherForecast("3 Jan 2022, 2:00pm"),
    createNewWeatherForecast("4 Jan 2022, 12:00am"),
    createNewWeatherForecast("9 Jan 2022, 12:00am"),
    createNewWeatherForecast("9 Jan 2022, 12:00am"),
    createNewWeatherForecast("9 Jan 2022, 12:00am")
)
const val queryLocation = "Lagos"

val invalidDataException = Exception("Invalid Data")
val networkExceptionLoadForecasts = NetworkException("Failed to load forecasts from network for city ID: 123")
val networkExceptionLoadSearchWeather = NetworkException("Failed to load search weather from network for location: $queryLocation")
val networkExceptionLoadWeather = NetworkException("Failed to load weather data from network with location: $dummyLocation")
val dbExceptionLoadWeather = DBException("Failed to load weather data from database with location: $dummyLocation")
val dbExceptionLoadForecasts = DBException("Failed to load forecasts from database for city ID: 123")
const val cityId = 1234