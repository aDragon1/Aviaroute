package self.adragon.aviaroute.data.repo

import self.adragon.aviaroute.data.database.dao.SearchFlightsDAO
import self.adragon.aviaroute.data.model.enums.SortOrder

class SearchFlightRepository(private val searchFlightsDAO: SearchFlightsDAO) {
    fun getSearchedFlights(
        departureAirportIndex: Int, destinationAirportIndex: Int,
        dayRange: Pair<Long, Long>, priceRange: Pair<Long, Long>, flightTimeRange: Pair<Long, Long>,
        order: SortOrder, errValue: Int
    ) = searchFlightsDAO.getSearchedFlights(
        departureAirportIndex, destinationAirportIndex, dayRange.first, dayRange.second,
        priceRange.first, priceRange.second, flightTimeRange.first, flightTimeRange.second,
        order, errValue
    )

    fun getClosestDate(
        departureAirportIndex: Int, destinationAirportIndex: Int, dayRange: Pair<Long, Long>,
        errValue: Int
    ): Pair<Long?, Long?> {
        val closestPastDate = searchFlightsDAO.getClosestPastDate(
            departureAirportIndex, destinationAirportIndex, dayRange.first, errValue
        )

        val closestFutureDate = searchFlightsDAO.getClosestFutureDate(
            departureAirportIndex, destinationAirportIndex, dayRange.second, errValue
        )
        return closestPastDate to closestFutureDate
    }

    fun getPriceRange(
        departureAirportIndex: Int, destinationAirportIndex: Int, dayRange: Pair<Long, Long>,
        errValue: Int
    ): Pair<Long, Long> {
        val dbPair = searchFlightsDAO.getPriceRange(
            departureAirportIndex,
            destinationAirportIndex,
            dayRange.first,
            dayRange.second,
            errValue
        )
        val a = dbPair.minValue ?: -1L
        val b = dbPair.maxValue ?: -1L
        return a to b
    }

    fun getFlightTimeRange(
        departureAirportIndex: Int, destinationAirportIndex: Int, dayRange: Pair<Long, Long>,
        errValue: Int
    ): Pair<Long, Long> {
        val dbPair = searchFlightsDAO.getFlightTimeRange(
            departureAirportIndex,
            destinationAirportIndex,
            dayRange.first,
            dayRange.second,
            errValue
        )
        val a = dbPair.minValue ?: -1L
        val b = dbPair.maxValue ?: -1L
        return a to b
    }
}
