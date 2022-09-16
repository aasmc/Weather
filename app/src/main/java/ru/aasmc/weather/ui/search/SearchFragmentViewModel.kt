package ru.aasmc.weather.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import ru.aasmc.weather.BuildConfig
import ru.aasmc.weather.data.model.SearchResult
import ru.aasmc.weather.data.model.Weather
import ru.aasmc.weather.data.source.repository.WeatherRepository
import ru.aasmc.weather.util.Result
import ru.aasmc.weather.util.asLiveData
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val repository: WeatherRepository
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

    private val _weatherInfo = MutableLiveData<Weather?>()
    val weatherInfo = _weatherInfo.asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    /**
     * Gets the [Weather] information for the user selected location[name]
     * @param name value of the location whose [Weather] data is to be fetched.
     */
    fun getSearchWeather(name: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            when (val result = repository.getSearchWeather(name)) {
                is Result.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                }
                Result.Loading -> _isLoading.value = true
                is Result.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        Timber.i("Weather Result ${result.data}")
                        _dataFetchState.value = true
                        _weatherInfo.postValue(result.data)
                    } else {
                        _weatherInfo.postValue(null)
                        _dataFetchState.postValue(false)
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