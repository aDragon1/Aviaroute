package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SearchFlightsDAO
import self.adragon.aviaroute.data.model.enums.SortOrder

class SearchFlightRepository(private val searchFlightsDAO: SearchFlightsDAO) {

    fun getSearchedFlights(
        departureAirportIndex: Int, destinationAirportIndex: Int, departureDateEpochSeconds: Long,
        order: SortOrder, errValue: Int
    ) = searchFlightsDAO.getSearchedFlights(
        departureAirportIndex, destinationAirportIndex,departureDateEpochSeconds, order, errValue
    )
}