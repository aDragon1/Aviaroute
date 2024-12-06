package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SearchFlightsDAO
import self.adragon.aviaroute.data.model.enums.SortOrder

class SearchFlightRepository(private val searchFlightsDAO: SearchFlightsDAO) {
    fun getSearchedFlights(
        departureAirportIndex: Int, destinationAirportIndex: Int, dayRange: Pair<Long, Long>,
        order: SortOrder, errValue: Int
    ) = searchFlightsDAO.getSearchedFlights(
        departureAirportIndex, destinationAirportIndex, dayRange.first, dayRange.second,
        order, errValue
    )

    fun getClosestDate(
        departureAirportIndex: Int, destinationAirportIndex: Int, departureDateEpochSeconds: Long,
        errValue: Int
    ): Pair<Long?, Long?> {
        val closestPastDate = searchFlightsDAO.getClosestPastDate(
            departureAirportIndex, destinationAirportIndex, departureDateEpochSeconds, errValue
        )

        val closestFutureDate = searchFlightsDAO.getClosestFutureDate(
            departureAirportIndex, destinationAirportIndex, departureDateEpochSeconds, errValue
        )
        return closestPastDate to closestFutureDate
    }
}
