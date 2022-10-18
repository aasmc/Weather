package ru.aasmc.weather.data.repository

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
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
import ru.aasmc.weather.domain.model.Weather
import ru.aasmc.weather.dummyLocation
import ru.aasmc.weather.fakeDbWeatherEntity
import ru.aasmc.weather.fakeNetworkWeather
import ru.aasmc.weather.fakeWeather
import ru.aasmc.weather.invalidDataException
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

            `when`(localDataSource.observeWeather()).thenReturn(
                flowOf(fakeDbWeatherEntity)
            )

            val response = systemUnderTest.observeWeather(dummyLocation, true)
                .filter {
                    it is Result.Success
                }.first()

            verify(remoteDataSource, times(1)).getWeather(dummyLocation)
            verify(localDataSource, times(1)).observeWeather()

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
        `when`(localDataSource.observeWeather()).thenReturn(
            flowOf(fakeDbWeatherEntity)
        )

        val response = systemUnderTest.observeWeather(dummyLocation, false)
            .filter {
                it is Result.Success
            }.first()

        verify(localDataSource, times(1)).observeWeather()
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
    fun `assert that getWeather with refresh as true returns fetches from the remote source but returns an Error`() = runTest {
        `when`(remoteDataSource.getWeather(dummyLocation)).thenReturn(
            Result.Error(
                invalidDataException
            )
        )

        val response = systemUnderTest.observeWeather(dummyLocation, true)
            .filter {
                it !is Result.Loading
            }.first()

        verify(remoteDataSource, times(1)).getWeather(dummyLocation)
        if (response is Result.Error) {
            assertEquals(invalidDataException, response.exception)
        }
    }
}























