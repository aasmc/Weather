package ru.aasmc.weather.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.aasmc.weather.data.local.database.WeatherDatabase
import ru.aasmc.weather.data.local.entity.ForecastDB
import ru.aasmc.weather.data.local.entity.WeatherConditionDB
import ru.aasmc.weather.data.local.entity.WeatherDB
import ru.aasmc.weather.data.local.entity.WeatherDescriptionDB
import ru.aasmc.weather.data.local.entity.WindDB
import ru.aasmc.weather.util.MainDispatcherRule
import ru.aasmc.weather.util.fakeDbWeatherEntity
import ru.aasmc.weather.util.fakeDbWeatherForecast

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    private lateinit var systemUnderTest: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        systemUnderTest = database.weatherDao
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertWeather_verifyWeatherDbIsNotEmpty() = runTest {
        systemUnderTest.insertWeather(fakeDbWeatherEntity)
        val weather = systemUnderTest.getWeather()
        assertNotNull(weather)
        assertEquals(fakeDbWeatherEntity.uId, weather.uId)
        assertEquals(fakeDbWeatherEntity.cityName, weather.cityName)
        assertEquals(fakeDbWeatherEntity.cityId, weather.cityId)
        assertEquals(fakeDbWeatherEntity.weatherCondition, weather.weatherCondition)
        assertEquals(fakeDbWeatherEntity.weatherDescriptions, weather.weatherDescriptions)
    }

    @Test
    fun insertWeatherWithSameId_ReplaceOnConflict_returnNewlyInsertedWeather() = runTest {
        val newWeatherEntity = WeatherDB(
            1,
            453,
            "Boston",
            WindDB(34.5, 43),
            listOf(WeatherDescriptionDB(2L, "Mains", "Clouds", "icons")),
            WeatherConditionDB(424.43, 3434.32, 23.5)
        )
        systemUnderTest.insertWeather(fakeDbWeatherEntity)

        systemUnderTest.insertWeather(newWeatherEntity)

        val weather = systemUnderTest.getWeather()
        assertNotNull(weather)
        assertEquals(newWeatherEntity.uId, weather.uId)
        assertEquals(newWeatherEntity.cityName, weather.cityName)
        assertEquals(newWeatherEntity.cityId, weather.cityId)
        assertEquals(newWeatherEntity.weatherCondition, weather.weatherCondition)
        assertEquals(newWeatherEntity.weatherDescriptions, weather.weatherDescriptions)
    }

    @Test
    fun insertForecastWeather_verifyForecastWeatherDbIsNotEmpty() = runTest {
        systemUnderTest.insertForecast(fakeDbWeatherForecast)

        val forecast = systemUnderTest.getAllForecasts().first()
        assertNotNull(forecast)
        assertEquals(fakeDbWeatherForecast.id, forecast.id)
        assertEquals(fakeDbWeatherForecast.date, forecast.date)
        assertEquals(fakeDbWeatherForecast.wind, forecast.wind)
        assertEquals(fakeDbWeatherForecast.networkWeatherCondition, forecast.networkWeatherCondition)
        assertEquals(fakeDbWeatherForecast.networkDescriptions, forecast.networkDescriptions)
    }

    @Test
    fun insertForecastWeatherWithSameId_ReplaceOnConflict_returnNewlyInsertedForecastWeather() = runTest {
        val newDbWeatherForecast = ForecastDB(
            1, "Dated", WindDB(42.2, 21),
            listOf(
                WeatherDescriptionDB(2L, "Mained", "Desces", "Icons")
            ),
            WeatherConditionDB(32.3, 52.2, 12.2)
        )

        systemUnderTest.insertForecast(fakeDbWeatherForecast)

        systemUnderTest.insertForecast(newDbWeatherForecast)

        val forecast = systemUnderTest.getAllForecasts().first()
        assertNotNull(forecast)
        assertEquals(newDbWeatherForecast.id, forecast.id)
        assertEquals(newDbWeatherForecast.date, forecast.date)
        assertEquals(newDbWeatherForecast.wind, forecast.wind)
        assertEquals(newDbWeatherForecast.networkWeatherCondition, forecast.networkWeatherCondition)
        assertEquals(newDbWeatherForecast.networkDescriptions, forecast.networkDescriptions)
    }

    @Test
    fun deleteWeather_returnNullValue() = runTest {
        systemUnderTest.insertWeather(fakeDbWeatherEntity)
        systemUnderTest.deleteAllWeather()

        val weather = systemUnderTest.getWeather()
        assertNull(weather)
    }

    @Test
    fun deleteForecastWeather_returnNullValue() = runTest {
        systemUnderTest.insertForecast(fakeDbWeatherForecast)
        systemUnderTest.deleteAllWeatherForecast()

        val forecast = systemUnderTest.getAllForecasts()
        assertTrue(forecast.isEmpty())
    }
}
























