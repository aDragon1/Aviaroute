package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

@Dao
interface SearchFlightsDAO {


    // TODO Set flight departure date not equals to selected date, but in range of 24 hours
    @Query(
        "WITH FlightBounds AS (     " +
                "     SELECT      " +
                "         f.flightIndex,     " +
                "         MIN(f.segmentPosition) AS firstSegmentPosition,     " +
                "         MAX(f.segmentPosition) AS lastSegmentPosition,     " +
                "         SUM(s.price) AS totalPrice,     " +
                "         SUM(s.flightTimeEpochSeconds) as totalTime,      " +
                "     GROUP_CONCAT(s.segmentIndex, ', ') AS allSegments, " +
                "     GROUP_CONCAT(a.code, ', ') AS allCodes  " +
                "     FROM      " +
                "         flights f     " +
                "     JOIN segments s ON f.segmentIndex = s.segmentIndex    " +
                " JOIN airports a on a.airportIndex = s.destinationIndex " +
                "     GROUP BY      " +
                "         f.flightIndex     " +
                " )     " +
                " SELECT      " +
                "   fb.flightIndex as flightIndex,      " +
                "   firstSegFlight.departureDateEpochSeconds as departureDateEpoch,     " +
                "   (firstSegFlight.departureDateEpochSeconds + fb.totalTime) as destinationDateEpoch,    " +
                "   fb.totalPrice,        " +
                "   fb.allSegments as flightSegments, " +
                "   (departureAirport.code || ', ' || fb.allCodes) as flightAirportCodes " +
                " from FlightBounds fb   " +
                "  " +
                " JOIN flights firstSegFlight ON firstSegFlight.flightIndex = fb.flightIndex  " +
                "                             AND firstSegFlight.segmentPosition = fb.firstSegmentPosition     " +
                " JOIN segments s1 on firstSegFlight.segmentIndex = s1.segmentIndex    " +
                " JOIN airports departureAirport on s1.departureIndex = departureAirport.airportIndex    " +
                "  " +
                " JOIN flights lastSegFlight ON lastSegFlight.flightIndex = fb.flightIndex  " +
                "                            AND lastSegFlight.segmentPosition = fb.lastSegmentPosition     " +
                " JOIN segments s2 on lastSegFlight.segmentIndex = s2.segmentIndex       " +
                " JOIN airports destinationAirport on destinationAirport.airportIndex = s2.destinationIndex  " +
                "  " +
                " WHERE " +
                "(:departureAirportIndex = :errValue OR " +
                "       departureAirport.airportIndex = :departureAirportIndex) " +
                " AND (:destinationAirportIndex = :errValue OR " +
                "       destinationAirport.airportIndex = :destinationAirportIndex) " +
                " AND departureDateEpoch >= :departureDateEpochSeconds " +

                "ORDER BY " +
                "CASE " +
                "WHEN :order = 'DEFAULT' THEN fb.flightIndex " +
                "WHEN :order = 'PRICE_UP' THEN fb.totalPrice " +
                "WHEN :order = 'DATE_UP' THEN firstSegFlight.departureDateEpochSeconds " +
                "END ASC, " +

                "CASE " +
                "WHEN :order = 'PRICE_DOWN' THEN fb.totalPrice " +
                "WHEN :order = 'DATE_DOWN' THEN firstSegFlight.departureDateEpochSeconds " +
                "END DESC"
    )
    fun getSearchedFlights(
        departureAirportIndex: Int, destinationAirportIndex: Int, departureDateEpochSeconds: Long,
        order: SortOrder, errValue: Int
    ): Flow<List<SearchResultFlight>>


    @Query(
        "WITH FlightBounds AS (     " +
                "     SELECT      " +
                "         f.flightIndex,     " +
                "         MIN(f.segmentPosition) AS firstSegmentPosition,     " +
                "         MAX(f.segmentPosition) AS lastSegmentPosition,     " +
                "         SUM(s.price) AS totalPrice,     " +
                "         SUM(s.flightTimeEpochSeconds) as totalTime,      " +
                "     GROUP_CONCAT(s.segmentIndex, ', ') AS allSegments, " +
                "     GROUP_CONCAT(a.code, ', ') AS allCodes  " +
                "     FROM      " +
                "         flights f     " +
                "     JOIN segments s ON f.segmentIndex = s.segmentIndex    " +
                " JOIN airports a on a.airportIndex = s.destinationIndex " +
                "     GROUP BY      " +
                "         f.flightIndex     " +
                " )     " +
                " SELECT min(firstSegFlight.departureDateEpochSeconds) from FlightBounds fb " +

                " JOIN flights firstSegFlight ON firstSegFlight.flightIndex = fb.flightIndex  " +
                "                             AND firstSegFlight.segmentPosition = fb.firstSegmentPosition     " +
                " JOIN segments s1 on firstSegFlight.segmentIndex = s1.segmentIndex    " +
                " JOIN airports departureAirport on s1.departureIndex = departureAirport.airportIndex    " +

                " JOIN flights lastSegFlight ON lastSegFlight.flightIndex = fb.flightIndex  " +
                "                            AND lastSegFlight.segmentPosition = fb.lastSegmentPosition     " +
                " JOIN segments s2 on lastSegFlight.segmentIndex = s2.segmentIndex       " +
                " JOIN airports destinationAirport on destinationAirport.airportIndex = s2.destinationIndex  " +

                " WHERE " +
                "(:departureAirportIndex = :errValue OR " +
                "       departureAirport.airportIndex = :departureAirportIndex) " +
                " AND (:destinationAirportIndex = :errValue OR " +
                "       destinationAirport.airportIndex = :destinationAirportIndex) " +
                " AND firstSegFlight.departureDateEpochSeconds > :departureDateEpochSeconds "
    )
    fun getClosestDate(
        departureAirportIndex: Int,
        destinationAirportIndex: Int,
        departureDateEpochSeconds: Long,
        errValue: Int
    ): Long
}
