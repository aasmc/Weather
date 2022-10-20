package ru.aasmc.weather.data.repository

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner
import ru.aasmc.weather.MainDispatcherRule
import ru.aasmc.weather.TestTransactionRunner
import ru.aasmc.weather.data.local.database.TransactionRunner
import ru.aasmc.weather.data.local.source.WeatherLocalDataSource
import ru.aasmc.weather.data.remote.WeatherRemoteDataSource
import ru.aasmc.weather.dbExceptionLoadForecasts
import ru.aasmc.weather.dbExceptionLoadWeather
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.dummyLocation
import ru.aasmc.weather.fakeDbWeatherEntity
import ru.aasmc.weather.fakeDbWeatherForecast
import ru.aasmc.weather.fakeNetworkWeather
import ru.aasmc.weather.fakeNetworkWeatherForecast
import ru.aasmc.weather.fakeWeather
import ru.aasmc.weather.fakeWeatherForecast
import ru.aasmc.weather.invalidDataException
import ru.aasmc.weather.networkExceptionLoadSearchWeather
import ru.aasmc.weather.networkExceptionLoadWeather
import ru.aasmc.weather.queryLocation
import ru.aasmc.weather.util.Result

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class WeatherRepositoryTest {
    @Mock
    private lateinit var remoteDataSource: WeatherRemoteDataSource

    @Mock
    private lateinit var localDataSource: WeatherLocalDataSource

    private lateinit var systemUnderTest: WeatherRepositoryImpl

    private val transactionRunner: TransactionRunner = TestTransactionRunner

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @Before
    fun setup() {
        systemUnderTest = WeatherRepositoryImpl(
            remoteDataSource,
            localDataSource,
            transactionRunner
        )
    }

    @Test
    fun `assert that getWeather with refresh as true fetches successfully from the remote source and local datasource as SSOT`() =
        runTest {
            `when`(remoteDataSource.getWeather(dummyLocation)).thenReturn(
                Result.Success(fakeNetworkWeather)
            )

            `when`(localDataSource.getWeather()).thenReturn(
                fakeDbWeatherEntity
            )

            val response = systemUnderTest.getWeather(dummyLocation, true)

            verify(remoteDataSource, times(1)).getWeather(dummyLocation)
            verify(localDataSource, times(1)).getWeather()

            if (response is Result.Success) {
                val weather = response.data
                assertThat(weather as Weather, `is`(notNullValue()))
                assertEquals(weather.name, fakeWeather.name)
                assertEquals(weather.cityId, fakeWeather.cityId)
                assertEquals(weather.wind, fakeWeather.wind)
                assertEquals(
                    weather.weatherCondition,
                    fakeWeather.weatherCondition
                )
                assertEquals(
                    weather.weatherDescriptions,
                    fakeWeather.weatherDescriptions
                )
            }
        }

    @Test
    fun `assert that getWeather with refresh as false fetches successfully from the local source`() = runTest {
        `when`(localDataSource.getWeather()).thenReturn(
            fakeDbWeatherEntity
        )

        val response = systemUnderTest.getWeather(dummyLocation, false)

        verify(localDataSource, times(1)).getWeather()
        verifyNoMoreInteractions(remoteDataSource)

        if (response is Result.Success) {
            val weather = response.data
            assertThat(weather as Weather, `is`(notNullValue()))
            assertEquals(weather.name, fakeWeather.name)
            assertEquals(weather.cityId, fakeWeather.cityId)
            assertEquals(weather.wind, fakeWeather.wind)
            assertEquals(
                weather.weatherCondition,
                fakeWeather.weatherCondition
            )
            assertEquals(
                weather.weatherDescriptions,
                fakeWeather.weatherDescriptions
            )
        }
    }

    @Test
    fun `assert that getWeather with refresh as true fetches from the remote source but returns an Error`() = runTest {
        `when`(remoteDataSource.getWeather(dummyLocation)).thenReturn(
            Result.Error(
                invalidDataException
            )
        )

        val response = systemUnderTest.getWeather(dummyLocation, true)

        verify(remoteDataSource, times(1)).getWeather(dummyLocation)
        if (response is Result.Error) {
            assertEquals(networkExceptionLoadWeather.message, response.exception.message)
        }
    }

    @Test
    fun `assert that with no weather in DB getWeather with refresh as false fetches DB error from repository`() = runTest {
        `when`(localDataSource.getWeather()).thenReturn(null)
        val response = systemUnderTest.getWeather(dummyLocation, false)
        verify(localDataSource, times(1)).getWeather()
        verifyNoMoreInteractions(remoteDataSource)
        assertTrue(response is Result.Error)
        assertEquals(dbExceptionLoadWeather.message, (response as Result.Error).exception.message)
    }

    @Test
    fun `assert that getForecastWeather with refresh as true fetches successfully`() = runTest {
        `when`(remoteDataSource.getWeatherForecast(fakeNetworkWeatherForecast.id))
            .thenReturn(Result.Success(listOf(fakeNetworkWeatherForecast)))

        `when`(localDataSource.getAllForecasts())
            .thenReturn(listOf(fakeDbWeatherForecast))

        val response = systemUnderTest.getForecasts(fakeNetworkWeatherForecast.id, true)
        verify(remoteDataSource, times(1)).getWeatherForecast(fakeNetworkWeatherForecast.id)
        verify(localDataSource, times(1)).getAllForecasts()

        assertTrue(response is Result.Success)
        val success = response as Result.Success
        assertNotNull(success.data)
        val forecast = success.data!!.first()
        assertEquals(forecast.date, fakeWeatherForecast.date)
        assertEquals(forecast.wind, fakeWeatherForecast.wind)
        assertEquals(forecast.id, fakeWeatherForecast.id)
        assertEquals(forecast.weatherCondition, fakeWeatherForecast.weatherCondition)
        assertEquals(forecast.weatherDescriptions, fakeWeatherForecast.weatherDescriptions)
    }

    @Test
    fun `assert that getForecasts with refresh as false fetches successfully from the local source`() = runTest {
        `when`(localDataSource.getAllForecasts()).thenReturn(
            listOf(fakeDbWeatherForecast)
        )
        val response = systemUnderTest.getForecasts(fakeNetworkWeatherForecast.id, false)
        verify(localDataSource, times(1)).getAllForecasts()
        verifyNoMoreInteractions(remoteDataSource)

        assertTrue(response is Result.Success)
        val success = response as Result.Success
        assertNotNull(success.data)
        val forecast = success.data!!.first()
        assertEquals(forecast.date, fakeWeatherForecast.date)
        assertEquals(forecast.wind, fakeWeatherForecast.wind)
        assertEquals(forecast.id, fakeWeatherForecast.id)
        assertEquals(forecast.weatherCondition, fakeWeatherForecast.weatherCondition)
        assertEquals(forecast.weatherDescriptions, fakeWeatherForecast.weatherDescriptions)
    }

    @Test
    fun `assert that getForecasts with refresh as false fetches null data from the local source`() = runTest {
        `when`(localDataSource.getAllForecasts()).thenReturn(
            null
        )

        val response = systemUnderTest.getForecasts(fakeDbWeatherEntity.cityId, false)

        verify(localDataSource, times(1)).getAllForecasts()
        verifyNoMoreInteractions(remoteDataSource)

        assertTrue(response is Result.Error)
        assertEquals(dbExceptionLoadForecasts.message, (response as Result.Error).exception.message)
    }

    @Test
    fun `assert that getSearchWeather fetches successfully from the remote source`() = runTest {
        `when`(remoteDataSource.getSearchWeather(queryLocation))
            .thenReturn(Result.Success(fakeNetworkWeather))

        val response = systemUnderTest.getSearchWeather(queryLocation)
        verify(remoteDataSource, times(1)).getSearchWeather(queryLocation)
        assertTrue(response is Result.Success)
        val weather = (response as Result.Success).data
        assertNotNull(weather)
        assertEquals(weather!!.name, fakeWeather.name)
        assertEquals(weather.cityId, fakeWeather.cityId)
        assertEquals(weather.wind, fakeWeather.wind)
        assertEquals(weather.weatherCondition, fakeWeather.weatherCondition)
        assertEquals(weather.weatherDescriptions, fakeWeather.weatherDescriptions)
    }

    @Test
    fun `assert that getSearchWeather fetches error from the remote source`() = runTest {
        `when`(remoteDataSource.getSearchWeather(queryLocation))
            .thenReturn(Result.Error(networkExceptionLoadSearchWeather))

        val response = systemUnderTest.getSearchWeather(queryLocation)
        verify(remoteDataSource, times(1)).getSearchWeather(queryLocation)
        assertTrue(response is Result.Error)
        val msg = (response as Result.Error).exception.message
        assertNotNull(msg)
        assertEquals(networkExceptionLoadSearchWeather.message, msg)
    }
}























