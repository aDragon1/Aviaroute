package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

@Dao
interface PurchasedDAO {
    @Query("SELECT * FROM purchased")
    fun getAllPurchased(): List<Purchased>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchased: Purchased)

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
                " WHERE fb.flightIndex = :flightIndex"
    )
    fun mapToSearchResult(flightIndex: Int): SearchResultFlight

    @Query("SELECT EXISTS(SELECT 1 FROM purchased WHERE flightIndex = :flightIndex)")
    fun contains(flightIndex:Int):Boolean
}

