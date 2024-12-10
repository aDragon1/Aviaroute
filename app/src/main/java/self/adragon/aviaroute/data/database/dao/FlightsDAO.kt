package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import self.adragon.aviaroute.data.model.Flight

@Dao
interface FlightsDAO {
    @Query("SELECT * FROM flights")
    fun getAllFlights(): List<Flight>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flight: Flight)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flights: List<Flight>)
}

