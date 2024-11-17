package self.adragon.aviaroute.data.repo

import androidx.lifecycle.LiveData
import self.adragon.aviaroute.data.database.dao.FlightsDAO
import self.adragon.aviaroute.data.model.Flight

class FlightsRepository(private val flightDAO: FlightsDAO) {
    fun getAllFlights()= flightDAO.getAllFlights()
    suspend fun insert(flight: Flight) = flightDAO.insert(flight)
}