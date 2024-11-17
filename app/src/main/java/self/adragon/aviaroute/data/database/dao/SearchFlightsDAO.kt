package self.adragon.aviaroute.data.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Query
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
                "         SUM(s.flightTime) as totalTime,      " +
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
                "   firstSegFlight.departureDate as departureDateEpoch,     " +
                "   (firstSegFlight.departureDate + fb.totalTime) as destinationDateEpoch,    " +
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
                "" +
                " AND (:destinationAirportIndex = :errValue OR " +
                "       destinationAirport.airportIndex = :destinationAirportIndex) "
    )
    fun getSearchedFlights(
        departureAirportIndex: Int,
        destinationAirportIndex: Int,
        errValue:Int
    ): LiveData<List<SearchResultFlight>>
}
