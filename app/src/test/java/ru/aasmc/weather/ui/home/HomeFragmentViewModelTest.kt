package ru.aasmc.weather.ui.home

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.aasmc.weather.MainDispatcherRule
import ru.aasmc.weather.dbExceptionLoadWeather
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.domain.usecases.GetWeather
import ru.aasmc.weather.dummyLocation
import ru.aasmc.weather.fakeWeather
import ru.aasmc.weather.networkExceptionLoadWeather
import ru.aasmc.weather.util.Result


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class HomeFragmentViewModelTest {

    private var repository: Repository = mock(Repository::class.java)

    private lateinit var getWeather: GetWeather

    private lateinit var systemUnderTest: HomeFragmentViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @Before
    fun setup() {
        getWeather = GetWeather(repository)
        systemUnderTest = HomeFragmentViewModel(getWeather)
    }

    @Test
    fun `assert that getWeather receives weather data from the repository successfully `() =
        runTest {
            `when`(repository.getWeather(dummyLocation, false)).thenReturn(
                Result.Success(fakeWeather)
            )

            systemUnderTest.handleEvent(HomeEvent.ObserveWeatherEvent(dummyLocation))
            verify(repository, times(1)).getWeather(dummyLocation, false)

            val uiState = systemUnderTest.homeViewState
                .filter { it is HomeViewState.WeatherDetails }
                .first()

            assertTrue(uiState is HomeViewState.WeatherDetails)
            val weather = (uiState as HomeViewState.WeatherDetails).weather

            assertEquals(fakeWeather, weather)
        }

    @Test
    fun `assert that getWeather receives null data from the repository `() = runTest {
        `when`(repository.getWeather(dummyLocation, false)).thenReturn(Result.Success(null))
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(Result.Success(null))

        systemUnderTest.handleEvent(HomeEvent.ObserveWeatherEvent(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, false)
        verify(repository, times(1)).getWeather(dummyLocation, true)

        val uiState = systemUnderTest.homeViewState
            .first()

        assertTrue(uiState is HomeViewState.Empty)
    }

    @Test
    fun `assert that getWeather receives a network error from the repository `() = runTest {
        `when`(repository.getWeather(dummyLocation, false)).thenReturn(Result.Success(null))
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(
            Result.Error(
                networkExceptionLoadWeather
            )
        )
        systemUnderTest.handleEvent(HomeEvent.ObserveWeatherEvent(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, false)
        verify(repository, times(1)).getWeather(dummyLocation, true)

        val uiState = systemUnderTest.homeViewState
            .filter { it is HomeViewState.Failure }
            .first()

        assertTrue(uiState is HomeViewState.Failure)
        val msg = (uiState as HomeViewState.Failure).throwable.message
        assertEquals(networkExceptionLoadWeather.message, msg)
    }

    @Test
    fun `assert that getWeather receives a DB error from the repository `() = runTest {
        `when`(repository.getWeather(dummyLocation, false)).thenReturn(Result.Success(null))
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(
            Result.Error(
                dbExceptionLoadWeather
            )
        )
        systemUnderTest.handleEvent(HomeEvent.ObserveWeatherEvent(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, false)
        verify(repository, times(1)).getWeather(dummyLocation, true)

        val uiState = systemUnderTest.homeViewState
            .filter { it is HomeViewState.Failure }
            .first()

        assertTrue(uiState is HomeViewState.Failure)
        val msg = (uiState as HomeViewState.Failure).throwable.message
        assertEquals(dbExceptionLoadWeather.message, msg)
    }

    @Test
    fun `assert that refreshWeather receives weather data from the repository successfully `() = runTest {
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(
            Result.Success(
                fakeWeather
            )
        )

        systemUnderTest.handleEvent(HomeEvent.RefreshWeather(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, true)

        val uiState = systemUnderTest.homeViewState
            .filter { it is HomeViewState.WeatherDetails }
            .first()

        assertTrue(uiState is HomeViewState.WeatherDetails)
        val weather = (uiState as HomeViewState.WeatherDetails).weather

        assertEquals(fakeWeather, weather)
    }

    @Test
    fun `assert that refreshWeather receives null from the repository shows empty state`() = runTest {
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(
            Result.Success(null)
        )

        systemUnderTest.handleEvent(HomeEvent.RefreshWeather(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, true)
        val uiState = systemUnderTest.homeViewState
            .first()

        assertTrue(uiState is HomeViewState.Empty)
    }

    @Test
    fun `assert that refreshWeather receives an error from the repository `() = runTest {
        `when`(repository.getWeather(dummyLocation, true)).thenReturn(
            Result.Error(
                dbExceptionLoadWeather
            )
        )

        systemUnderTest.handleEvent(HomeEvent.RefreshWeather(dummyLocation))
        verify(repository, times(1)).getWeather(dummyLocation, true)

        val uiState = systemUnderTest.homeViewState
            .filter { it is HomeViewState.Failure }
            .first()

        assertTrue(uiState is HomeViewState.Failure)
        val msg = (uiState as HomeViewState.Failure).throwable.message
        assertEquals(dbExceptionLoadWeather.message, msg)
    }
}





















