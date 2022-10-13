package ru.aasmc.weather.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import ru.aasmc.weather.BuildConfig
import ru.aasmc.weather.domain.model.SearchResult
import ru.aasmc.weather.domain.usecases.ObserveSearchWeather
import ru.aasmc.weather.util.Result
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val observeSearchWeather: ObserveSearchWeather
) : ViewModel() {
    private val applicationID = BuildConfig.ALGOLIA_APP_ID
    private val algoliaAPIKey = BuildConfig.ALGOLIA_API_KEY
    private val algoliaIndexName = BuildConfig.ALGOLIA_INDEX_NAME

    private val client = ClientSearch(
        ApplicationID(applicationID),
        APIKey(algoliaAPIKey)
    )

    private val index = client.initIndex(IndexName(algoliaIndexName))
    private val searcher = SearcherSingleIndex(index)

    private val dataSourceFactory =
        SearcherSingleIndexDataSource.Factory(searcher) { hit ->
            SearchResult(
                name = hit["name"]?.jsonPrimitive?.content ?: "",
                subcountry = hit["subcountry"]?.jsonPrimitive?.content ?: "",
                country = hit["country"]?.jsonPrimitive?.content ?: ""
            )
        }

    private val pagedListConfig =
        PagedList.Config.Builder().setPageSize(50).build()

    val locations: LiveData<PagedList<SearchResult>> =
        LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox =
        SearchBoxConnectorPagedList(searcher, listOf(locations))
    val stats = StatsConnector(searcher)
    private val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats
    }

    private val _viewState: MutableStateFlow<SearchViewState> =
        MutableStateFlow(SearchViewState.Empty)
    val viewState: StateFlow<SearchViewState> = _viewState.asStateFlow()

    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.SearchForWeather -> {
                displayWeatherResult(event.name)
            }
        }
    }

    private fun displayWeatherResult(name: String) {
        viewModelScope.launch {
            observeSearchWeather(name)
                .collect { result ->
                    when (result) {
                        is Result.Error -> {
                            _viewState.update {
                                SearchViewState.Failure
                            }
                        }
                        Result.Loading -> {
                            _viewState.update {
                                SearchViewState.Loading
                            }
                        }
                        is Result.Success -> {
                            if (result.data != null) {
                                Timber.i("Weather Result ${result.data}")
                                _viewState.update {
                                    SearchViewState.WeatherDetails(result.data)
                                }
                            } else {
                                _viewState.update {
                                    SearchViewState.Empty
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }
}