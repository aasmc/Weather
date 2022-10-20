package ru.aasmc.weather.ui.forecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.shrikanthravi.collapsiblecalendarview.data.Day
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import ru.aasmc.weather.MainDispatcherRule
import ru.aasmc.weather.cityId
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.domain.usecases.GetForecasts
import ru.aasmc.weather.fakeWeatherForecast
import ru.aasmc.weather.fakeWeatherForecastList
import ru.aasmc.weather.networkExceptionLoadForecasts
import ru.aasmc.weather.util.Result

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ForecastFragmentViewModelTest {
    @Mock
    private lateinit var repository: Repository

    private lateinit var systemUnderTest: ForecastFragmentViewModel

    private lateinit var getForecasts: GetForecasts

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    @Before
    fun setup() {
        getForecasts = GetForecasts(repository)
        systemUnderTest = ForecastFragmentViewModel(getForecasts)
    }

    @Test
    fun `assert that getForecast with refresh as false receives successful response from the repository`() =
        runTest {
            `when`(repository.getForecasts(cityId, false)).thenReturn(
                Result.Success(
                    listOf(
                        fakeWeatherForecast
                    )
                )
            )

            systemUnderTest.handleEvent(ForecastEvent.ObserveForecast(cityId))
            verify(repository, times(1)).getForecasts(cityId, false)

            val uiState = systemUnderTest.forecastViewState
                .filter { it is ForecastViewState.Success }
                .first()

            assertTrue(uiState is ForecastViewState.Success)
            val list = (uiState as ForecastViewState.Success).forecasts

            assertEquals(listOf(fakeWeatherForecast), list)
        }

    @Test
    fun `assert that getForecast with refresh as false receives a null value as response`() =
        runTest {
            `when`(
                repository.getForecasts(
                    cityId,
                    false
                )
            ).thenReturn(Result.Success(null))
            `when`(repository.getForecasts(cityId, true)).thenReturn(Result.Success(null))

            systemUnderTest.handleEvent(ForecastEvent.ObserveForecast(cityId))
            verify(repository, times(1)).getForecasts(cityId, false)
            verify(repository, times(1)).getForecasts(cityId, true)

            val uiState = systemUnderTest.forecastViewState
                .first()

            assertTrue(uiState is ForecastViewState.Empty)
        }

    @Test
    fun `assert that getForecast with refresh as true receives a network error response`() =
        runTest {
            `when`(
                repository.getForecasts(
                    cityId,
                    false
                )
            ).thenReturn(Result.Success(null))
            `when`(repository.getForecasts(cityId, true)).thenReturn(
                Result.Error(
                    networkExceptionLoadForecasts
                )
            )

            systemUnderTest.handleEvent(ForecastEvent.ObserveForecast(cityId))
            verify(repository, times(1)).getForecasts(cityId, false)
            verify(repository, times(1)).getForecasts(cityId, true)

            val uiState = systemUnderTest.forecastViewState
                .filter {
                    it is ForecastViewState.Failure
                }.first()

            assertTrue(uiState is ForecastViewState.Failure)
            val msg = (uiState as ForecastViewState.Failure).exception.message

            assertEquals(networkExceptionLoadForecasts.message, msg)
        }

    @Test
    fun `assert that refreshForecast receives successful response from the repository`() =
        runTest {
            `when`(repository.getForecasts(cityId, true)).thenReturn(
                Result.Success(
                    listOf(
                        fakeWeatherForecast
                    )
                )
            )

            systemUnderTest.handleEvent(ForecastEvent.RefreshForecast(cityId))
            verify(repository, times(1)).getForecasts(cityId, true)
            val uiState = systemUnderTest.forecastViewState
                .filter { it is ForecastViewState.Success }
                .first()

            assertTrue(uiState is ForecastViewState.Success)
            val list = (uiState as ForecastViewState.Success).forecasts
            assertEquals(listOf(fakeWeatherForecast), list)
        }

    @Test
    fun `assert that refreshForecastData receives a network error response`() = runTest {
        `when`(repository.getForecasts(cityId, true)).thenReturn(
            Result.Error(
                networkExceptionLoadForecasts
            )
        )

        systemUnderTest.handleEvent(ForecastEvent.RefreshForecast(cityId))
        verify(repository, times(1)).getForecasts(cityId, true)
        val uiState = systemUnderTest.forecastViewState
            .filter {
                it is ForecastViewState.Failure
            }.first()

        assertTrue(uiState is ForecastViewState.Failure)
        val msg = (uiState as ForecastViewState.Failure).exception.message

        assertEquals(networkExceptionLoadForecasts.message, msg)
    }

    @Test
    fun `assert that refreshForecastData receives a null value as response`() = runTest {
        `when`(repository.getForecasts(cityId, true))
            .thenReturn(Result.Success(null))

        systemUnderTest.handleEvent(ForecastEvent.RefreshForecast(cityId))
        verify(repository, times(1)).getForecasts(cityId, true)

        val uiState = systemUnderTest.forecastViewState
            .first()

        assertTrue(uiState is ForecastViewState.Empty)
    }

    @Test
    fun `assert that updateWeatherForecast returns a correctly filtered list`() =
        runTest {
            `when`(repository.getForecasts(cityId, false)).thenReturn(
                Result.Success(
                    fakeWeatherForecastList
                )
            )

            val day = Day(2022, 0, 9)
            systemUnderTest.handleEvent(ForecastEvent.UpdateWeatherForecast(day, cityId))

            verify(repository, times(1)).getForecasts(cityId, false)
            val uiState = systemUnderTest.forecastViewState
                .filter { it is ForecastViewState.FilteredForecast }
                .first()

            assertTrue(uiState is ForecastViewState.FilteredForecast)
            val list = (uiState as ForecastViewState.FilteredForecast).filteredForecasts
            assertEquals(3, list.size)
        }
}


















