package ru.aasmc.weather.ui.search

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
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import ru.aasmc.weather.MainDispatcherRule
import ru.aasmc.weather.domain.repositories.Repository
import ru.aasmc.weather.domain.usecases.GetSearchWeather
import ru.aasmc.weather.fakeWeather
import ru.aasmc.weather.networkExceptionLoadSearchWeather
import ru.aasmc.weather.queryLocation
import ru.aasmc.weather.util.Result

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragmentViewModelTest {
    @Mock
    private lateinit var repository: Repository

    private lateinit var getSearchWeather: GetSearchWeather

    private lateinit var systemUnderTest: SearchFragmentViewModel

    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        getSearchWeather = GetSearchWeather(repository)
        systemUnderTest = SearchFragmentViewModel(getSearchWeather)
    }

    @Test
    fun `assert that getSearchWeather returns the weather result successfully from the repository`() = runTest {
        `when`(repository.getSearchWeather(queryLocation)).thenReturn(
            Result.Success(
                fakeWeather
            )
        )

        systemUnderTest.handleEvent(SearchEvent.SearchForWeather(queryLocation))
        verify(repository, times(1)).getSearchWeather(queryLocation)
        val uiState = systemUnderTest.viewState
            .filter { it is SearchViewState.WeatherDetails }
            .first()

        assertTrue(uiState is SearchViewState.WeatherDetails)
        val weather = (uiState as SearchViewState.WeatherDetails).weather
        assertEquals(fakeWeather, weather)
    }

    @Test
    fun `assert that getSearchWeather returns a null result from the repository`() = runTest {
        `when`(repository.getSearchWeather(queryLocation)).thenReturn(
            Result.Success(
                null
            )
        )
        systemUnderTest.handleEvent(SearchEvent.SearchForWeather(queryLocation))
        verify(repository, times(1)).getSearchWeather(queryLocation)
        val uiState = systemUnderTest.viewState
            .filter { it is SearchViewState.Hidden }
            .first()
        assertTrue(uiState is SearchViewState.Hidden)
    }

    @Test
    fun `assert that getSearchWeather returns an error from the repository`() = runTest {
        `when`(repository.getSearchWeather(queryLocation)).thenReturn(
            Result.Error(
                networkExceptionLoadSearchWeather
            )
        )
        systemUnderTest.handleEvent(SearchEvent.SearchForWeather(queryLocation))
        verify(repository, times(1)).getSearchWeather(queryLocation)
        val uiState = systemUnderTest.viewState
            .filter { it is SearchViewState.Failure }
            .first()
        assertTrue(uiState is SearchViewState.Failure)
        val msg = (uiState as SearchViewState.Failure).throwable.message
        assertEquals(networkExceptionLoadSearchWeather.message, msg)
    }
}
























