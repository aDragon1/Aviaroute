package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.repo.SearchFlightRepository

class SearchResulViewModel(private val application: Application) : AndroidViewModel(application) {

    private val mediatorLiveData: MediatorLiveData<List<SearchResultFlight>> = MediatorLiveData()
    val searchResult: MutableLiveData<List<SearchResultFlight>> get() = mediatorLiveData

    var sortOrder: MutableLiveData<SortOrder> = MutableLiveData(SortOrder.DEFAULT)

    private val errorValue = -1

    init {
        mediatorLiveData.addSource(sortOrder) {
            Log.d("mytag", "Sort order changed")
            viewModelScope.launch { getSearchResult() }
        }
    }

    private var lastDepAirportIndex = -1
    private var lastDestAirportIndex = -1
    private var departureDateEpochSeconds = -1L

    fun setSearchResult(
        departureAirportIndex: Int,
        destinationAirportIndex: Int,
        epochSeconds: Long
    ) {
        lastDepAirportIndex = departureAirportIndex
        lastDestAirportIndex = destinationAirportIndex
        departureDateEpochSeconds = epochSeconds

        getSearchResult()
    }

    private fun getSearchResult() = CoroutineScope(Dispatchers.IO).launch {
        searchResult.postValue(emptyList())

        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        val searchFlightsRepository = SearchFlightRepository(db.searchFlightsDAO())

        val searchResultFlightsFlow = searchFlightsRepository.getSearchedFlights(
            lastDepAirportIndex, lastDestAirportIndex,departureDateEpochSeconds, sortOrder.value!!, errorValue
        )

        searchResultFlightsFlow.onEach {
            val currentList = searchResult.value?.toMutableList() ?: mutableListOf()
            currentList.addAll(it)
            searchResult.postValue(currentList)
        }.catch { e -> Log.e("mytag", e.message ?: "") }.collect()
    }

    fun setSortOrder(order: SortOrder): Boolean {
        if (sortOrder.value != order) {
            sortOrder.value = order
            return true
        }
        return false
    }
}
