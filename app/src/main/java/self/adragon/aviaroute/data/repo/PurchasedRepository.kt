package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.PurchasedDAO
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

class PurchasedRepository(private val purchasedDAO: PurchasedDAO) {
    fun getAllPurchased() = purchasedDAO.getAllPurchased()
    suspend fun insert(purchased: Purchased) = purchasedDAO.insert(purchased)

    fun mapToSearchResult(flightIndex: Int) = purchasedDAO.mapToSearchResult(flightIndex)
    fun contains(flight: SearchResultFlight) = purchasedDAO.contains(flight.flightIndex)
}