package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import self.adragon.aviaroute.data.model.DBPair
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

@Dao
interface SearchFlightsDAO {


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
                "   (:departureIndex = :errValue OR departureAirport.airportIndex = :departureIndex) " +
                " AND " +
                "   (:destinationIndex = :errValue OR destinationAirport.airportIndex = :destinationIndex) " +
                " AND " +
                "       (firstSegFlight.departureDateEpochSeconds >= :dayStart " +
                "   AND firstSegFlight.departureDateEpochSeconds <= :dayEnd) " +
                " AND  (fb.totalPrice >= :minPrice OR :minPrice = :errValue)" +
                " AND  (fb.totalPrice <= :maxPrice OR :maxPrice = :errValue)" +
                " AND  (fb.totalTime >= :minFlightTime OR :minFlightTime = :errValue)" +
                " AND  (fb.totalTime <= :maxFlightTime OR :maxFlightTime = :errValue)" +

                "ORDER BY " +
                "CASE " +
                "   WHEN :order = 'DEFAULT' THEN fb.flightIndex " +
                "   WHEN :order = 'PRICE_UP' THEN fb.totalPrice " +
                "   WHEN :order = 'DATE_UP' THEN firstSegFlight.departureDateEpochSeconds " +
                "END ASC, " +

                "CASE " +
                "   WHEN :order = 'PRICE_DOWN' THEN fb.totalPrice " +
                "   WHEN :order = 'DATE_DOWN' THEN firstSegFlight.departureDateEpochSeconds " +
                "END DESC"
    )
    fun getSearchedFlights(
        departureIndex: Int, destinationIndex: Int, dayStart: Long, dayEnd: Long,
        minPrice: Long, maxPrice: Long, minFlightTime: Long, maxFlightTime: Long,
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
                "   (:departureIndex = :errValue OR departureAirport.airportIndex = :departureIndex) " +
                " AND " +
                "   (:destinationIndex = :errValue OR destinationAirport.airportIndex = :destinationIndex) " +
                " AND " +
                "   firstSegFlight.departureDateEpochSeconds > :epochSeconds "
    )
    fun getClosestFutureDate(
        departureIndex: Int, destinationIndex: Int, epochSeconds: Long, errValue: Int
    ): Long?

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
                " SELECT max(firstSegFlight.departureDateEpochSeconds) from FlightBounds fb " +

                " JOIN flights firstSegFlight ON firstSegFlight.flightIndex = fb.flightIndex  " +
                "                             AND firstSegFlight.segmentPosition = fb.firstSegmentPosition     " +
                " JOIN segments s1 on firstSegFlight.segmentIndex = s1.segmentIndex    " +
                " JOIN airports departureAirport on s1.departureIndex = departureAirport.airportIndex    " +

                " JOIN flights lastSegFlight ON lastSegFlight.flightIndex = fb.flightIndex  " +
                "                            AND lastSegFlight.segmentPosition = fb.lastSegmentPosition     " +
                " JOIN segments s2 on lastSegFlight.segmentIndex = s2.segmentIndex       " +
                " JOIN airports destinationAirport on destinationAirport.airportIndex = s2.destinationIndex  " +

                " WHERE " +
                "   (:departureIndex = :errValue OR departureAirport.airportIndex = :departureIndex) " +
                " AND " +
                "   (:destinationIndex = :errValue OR destinationAirport.airportIndex = :destinationIndex) " +
                " AND " +
                "   firstSegFlight.departureDateEpochSeconds < :epochSeconds "
    )
    fun getClosestPastDate(
        departureIndex: Int, destinationIndex: Int, epochSeconds: Long, errValue: Int
    ): Long?

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
                "   MIN(fb.totalPrice) as minValue, MAX(fb.totalPrice) as maxValue " +
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
                "   (:departureIndex = :errValue OR departureAirport.airportIndex = :departureIndex) " +
                " AND " +
                "   (:destinationIndex = :errValue OR destinationAirport.airportIndex = :destinationIndex) " +
                " AND " +
                "       (firstSegFlight.departureDateEpochSeconds >= :dayStart " +
                "   AND firstSegFlight.departureDateEpochSeconds <= :dayEnd) "
    )
    fun getPriceRange(
        departureIndex: Int, destinationIndex: Int, dayStart: Long, dayEnd: Long, errValue: Int
    ): DBPair

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
                "   MIN(fb.totalTime) as minValue, MAX(fb.totalTime) as maxValue " +
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
                "   (:departureIndex = :errValue OR departureAirport.airportIndex = :departureIndex) " +
                " AND " +
                "   (:destinationIndex = :errValue OR destinationAirport.airportIndex = :destinationIndex) " +
                " AND " +
                "       (firstSegFlight.departureDateEpochSeconds >= :dayStart " +
                "   AND firstSegFlight.departureDateEpochSeconds <= :dayEnd) "
    )
    fun getFlightTimeRange(
        departureIndex: Int, destinationIndex: Int, dayStart: Long, dayEnd: Long, errValue: Int
    ): DBPair
}