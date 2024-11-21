package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.AirportsDAO
import self.adragon.aviaroute.data.model.Airport

class AirportRepository(private val airportsDAO: AirportsDAO) {
    fun getAllAirports() = airportsDAO.getAllAirports()

    private fun getAirportByIndex(index: Int) = airportsDAO.getAirportByIndex(index)

    fun getAirportByIndexes(indexes: List<Int>) = indexes.map { getAirportByIndex(it) }

    suspend fun insert(airport: Airport) = airportsDAO.insert(airport)
}