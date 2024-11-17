package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.repo.SearchFlightRepository

class SearchResulViewModel(private val application: Application) : AndroidViewModel(application) {

    private lateinit var _searchResult: LiveData<List<SearchResultFlight>>
    val searchResult = MediatorLiveData<List<SearchResultFlight>>(listOf())
    val clickedItem = MutableLiveData<SearchResultFlight>()

    fun setSearchResult(departureAirportIndex: Int, destinationAirportIndex: Int, errValue: Int) {
        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        val searchFlightsDAO = db.searchFlightsDAO()
        val searchFlightsRepository = SearchFlightRepository(searchFlightsDAO)

        _searchResult = searchFlightsRepository.getSearchedFlights(
            departureAirportIndex, destinationAirportIndex, errValue
        )
        searchResult.addSource(_searchResult) { searchResult.value = _searchResult.value }
    }

    fun setClickedItem(searchRes: SearchResultFlight) {
        clickedItem.value = searchRes
    }
}
