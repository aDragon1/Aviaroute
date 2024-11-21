package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.repo.SearchFlightRepository

class SearchResulViewModel(private val application: Application) : AndroidViewModel(application) {

    lateinit var searchResult: LiveData<List<SearchResultFlight>>
    val clickedItem = MutableLiveData<SearchResultFlight>()

    fun setSearchResult(departureAirportIndex: Int, destinationAirportIndex: Int, errValue: Int) {
        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        val searchFlightsRepository = SearchFlightRepository(db.searchFlightsDAO())

        searchResult = searchFlightsRepository.getSearchedFlights(
            departureAirportIndex, destinationAirportIndex, errValue
        )
    }

    fun setClickedItem(searchRes: SearchResultFlight) {
        clickedItem.value = searchRes
    }
}
