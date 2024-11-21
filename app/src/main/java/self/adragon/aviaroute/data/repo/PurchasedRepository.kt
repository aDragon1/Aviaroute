package self.adragon.aviaroute.data.repo

import androidx.lifecycle.LiveData
import self.adragon.aviaroute.data.database.dao.PurchasedDAO
import self.adragon.aviaroute.data.model.Purchased

class PurchasedRepository(private val purchasedDAO: PurchasedDAO) {
    fun getAllPurchased() = purchasedDAO.getAllPurchased()

    fun mapToSearchResult(flightIndex: Int) = purchasedDAO.mapToSearchResult(flightIndex)
    suspend fun insert(purchased: Purchased) = purchasedDAO.insert(purchased)
}