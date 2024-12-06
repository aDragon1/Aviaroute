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
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.data.repo.SearchFlightRepository

class SearchResulViewModel(application: Application) : AndroidViewModel(application) {

    private val mediatorLiveData: MediatorLiveData<List<SearchResultFlight>> = MediatorLiveData()
    val searchResult: MutableLiveData<List<SearchResultFlight>> get() = mediatorLiveData

    var sortOrder: MutableLiveData<SortOrder> = MutableLiveData(SortOrder.DATE_UP)

    private val errorValue = -1

    private val searchFlightsRepository: SearchFlightRepository

    init {
        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        searchFlightsRepository = SearchFlightRepository(db.searchFlightsDAO())

        mediatorLiveData.addSource(sortOrder) { viewModelScope.launch { getSearchResult() } }
    }

    private var departureAirportIndex = -1
    private var destinationAirportIndex = -1
    var departureDateEpochSeconds = -1L

    fun setSearchResult(departureIndex: Int, destinationIndex: Int, epochSeconds: Long) {
        departureAirportIndex = departureIndex
        destinationAirportIndex = destinationIndex
        departureDateEpochSeconds = epochSeconds

        getSearchResult()
    }

    private fun getSearchResult() = CoroutineScope(Dispatchers.IO).launch {
        searchResult.postValue(emptyList())

        val dayRange = LocalDateConverter().getDayRange(departureDateEpochSeconds)
        val searchResultFlightsFlow = searchFlightsRepository.getSearchedFlights(
            departureAirportIndex, destinationAirportIndex, dayRange, sortOrder.value!!, errorValue
        )

        searchResultFlightsFlow.onEach {
            val currentList = searchResult.value?.toMutableList() ?: mutableListOf()
            currentList.addAll(it)
            searchResult.postValue(currentList)
        }.catch { e -> Log.e("mytag", e.message ?: "") }.collect()
    }

    fun getClosestDate(
        departureAirportIndex: Int, destinationAirportIndex: Int,
        epochSeconds: Long
    ) = searchFlightsRepository.getClosestDate(
        departureAirportIndex, destinationAirportIndex, epochSeconds, errorValue
    )


    fun setSortOrder(order: SortOrder): Boolean {
        if (sortOrder.value != order) {
            sortOrder.value = order
            return true
        }
        return false
    }

    fun clear() {
        searchResult.postValue(emptyList())

        departureAirportIndex = -1
        destinationAirportIndex = -1
        departureDateEpochSeconds = -1L
    }
}
