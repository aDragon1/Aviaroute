package self.adragon.aviaroute.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                }.sortedBy { it.departureDateEpoch }
                closestFlight = res.minByOrNull { it.destinationDateEpoch }
                resultLiveData.postValue(res)
            }
            resultLiveData
        }
    private var closestFlight: SearchResultFlight? = null

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

    fun contains(clicked: SearchResultFlight): Boolean = purchasedRepository.contains(clicked)

//    // TODO Do stuff
//    fun scheduleNotification(context: Context, destinationDateEpoch: Long, flightId: Int) {
//        val currentTimeMillis = System.currentTimeMillis()
//        val notificationTimeMillis =
//            destinationDateEpoch * 1000 - 60 * 60 * 1000 // 10 минут до события
//
//        if (notificationTimeMillis <= currentTimeMillis) {
//            Log.d("Notification", "Событие уже прошло или время слишком близко.")
//            return
//        }
//
//        val delayMillis = notificationTimeMillis - currentTimeMillis
//
//        val inputData = Data.Builder()
//            .putString("title", "Flight Reminder")
//            .putString("text", "Your flight will arrive in 10 minutes.")
//            .putInt("notificationId", flightId)
//            .build()
//
//        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
//            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
//            .setInputData(inputData)
//            .build()
//
//        WorkManager.getInstance(context).enqueue(workRequest)
//    }
}