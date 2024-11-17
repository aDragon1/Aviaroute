package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SearchFlightsDAO

class SearchFlightRepository(private val searchFlightsDAO: SearchFlightsDAO) {

    fun getSearchedFlights(
        departureAirportIndex: Int, destinationAirportIndex: Int, errValue: Int
    ) =
        searchFlightsDAO.getSearchedFlights(
            departureAirportIndex, destinationAirportIndex, errValue
        )
}