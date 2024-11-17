package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import self.adragon.aviaroute.data.model.Airport

@Dao
interface AirportsDAO {
    @Query("SELECT * FROM airports")
    fun getAllAirports(): List<Airport>

    @Query("SELECT * FROM airports where airportIndex = :index")
    fun getAirportByIndex(index: Int): Airport?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(airport: Airport)
}