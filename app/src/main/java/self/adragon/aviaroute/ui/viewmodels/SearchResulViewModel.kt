package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.data.repo.SearchFlightRepository

class SearchResulViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchResultFlightsFlow: MutableStateFlow<List<SearchResultFlight>> =
        MutableStateFlow(emptyList())
    val searchResultFlightsFlow = _searchResultFlightsFlow.asStateFlow()

    private var _sortOrder = MutableStateFlow(SortOrder.DATE_UP)
    var sortOrder: StateFlow<SortOrder> = _sortOrder

    private val errorValue = -1

    private val searchFlightsRepository: SearchFlightRepository

    var departureAirportIndex = -1
    var destinationAirportIndex = -1
    var departureDateEpochSeconds = -1L
    private var dayRange = -1L to -1L

    private var priceRange = -1L to -1L
    private var flightTimeRange = -1L to -1L

    private var curPriceRange = -1L to -1L
    private var curFlightTimeRange = -1L to -1L

    init {
        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        searchFlightsRepository = SearchFlightRepository(db.searchFlightsDAO())

        viewModelScope.launch { _sortOrder.collect { getSearchResult() } }
    }

    fun setSearchResult(departureIndex: Int, destinationIndex: Int, epochSeconds: Long) {
        departureAirportIndex = departureIndex
        destinationAirportIndex = destinationIndex
        departureDateEpochSeconds = epochSeconds
        dayRange = LocalDateConverter().getDayRange(epochSeconds)

        val logMessage = "Search summary:\n" +
                "   departureAirportIndex = $departureAirportIndex\n" +
                "   destinationAirportIndex = $destinationAirportIndex\n" +
                "   dayStart = ${dayRange.first}\n" +
                "   dayEnd = ${dayRange.second}"
        Log.d("mytag", logMessage)

        getSearchResult()
    }

    private fun getSearchResult() = viewModelScope.launch {
        searchFlightsRepository.getSearchedFlights(
            departureAirportIndex,
            destinationAirportIndex,
            dayRange,
            curPriceRange,
            curFlightTimeRange,
            sortOrder.value,
            errorValue
        ).flowOn(Dispatchers.IO).collect { flights -> _searchResultFlightsFlow.update { flights } }

        priceRange = searchFlightsRepository.getPriceRange(
            departureAirportIndex, destinationAirportIndex, dayRange, errorValue
        )
        flightTimeRange = searchFlightsRepository.getFlightTimeRange(
            departureAirportIndex, destinationAirportIndex, dayRange, errorValue
        )
    }

    fun getClosestDate() = searchFlightsRepository.getClosestDate(
        departureAirportIndex, destinationAirportIndex, dayRange, errorValue
    )

    fun getPriceRange(): Pair<Pair<Long, Long>, Pair<Long, Long>> {

        val a = if (priceRange.first != -1L) priceRange
        else searchFlightsRepository.getPriceRange(
            departureAirportIndex, destinationAirportIndex, dayRange, errorValue
        )
        val b = if (curPriceRange.first == -1L) a else curPriceRange
        return a to b
    }

    fun getFlightTimeRange(): Pair<Pair<Long, Long>, Pair<Long, Long>> {
        val a = if (flightTimeRange.first != -1L) flightTimeRange
        else searchFlightsRepository.getFlightTimeRange(
            departureAirportIndex, destinationAirportIndex, dayRange, errorValue
        )
        val b = if (curFlightTimeRange.first == -1L) a else curFlightTimeRange
        return a to b
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setPriceFlightTimeRange(minMax: Pair<Long, Long>, isPrice: Boolean) {
        if (isPrice) curPriceRange = minMax
        else curFlightTimeRange = minMax

        getSearchResult()
    }


    fun clear() {
        _searchResultFlightsFlow.value = emptyList()

        departureAirportIndex = -1
        destinationAirportIndex = -1
        departureDateEpochSeconds = -1L

        priceRange = -1L to -1L
        flightTimeRange = -1L to -1L
        curPriceRange = -1L to -1L
        curFlightTimeRange = -1L to -1L
    }
}
