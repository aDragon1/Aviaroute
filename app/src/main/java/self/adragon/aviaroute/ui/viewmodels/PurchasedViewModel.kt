package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.repo.PurchasedRepository

class PurchasedViewModel(application: Application) : AndroidViewModel(application) {
    var purchasedLiveData: MutableLiveData<List<Purchased>> = MutableLiveData()
    val purchasedSearchResultLiveData: LiveData<List<SearchResultFlight>> =
        purchasedLiveData.switchMap {
            val resultLiveData = MutableLiveData<List<SearchResultFlight>>(emptyList())
            CoroutineScope(Dispatchers.IO).launch {
                val res = it.map { purchased ->
                    purchasedRepository.mapToSearchResult(purchased.flightIndex)
                }
                resultLiveData.postValue(res)
            }
            resultLiveData
        }

    private var purchasedRepository: PurchasedRepository


    init {
        val db: FlightsDatabase = FlightsDatabase.getDatabase(application)
        purchasedRepository = PurchasedRepository(db.purchasedDAO())

        CoroutineScope(Dispatchers.IO).launch {
            val lst = purchasedRepository.getAllPurchased()
            purchasedLiveData.postValue(lst)
        }
    }

    fun insert(purchased: Purchased) = viewModelScope.launch {
        purchasedRepository.insert(purchased)

        CoroutineScope(Dispatchers.IO).launch {
            val lst = purchasedRepository.getAllPurchased()
            purchasedLiveData.postValue(lst)
        }
    }
}