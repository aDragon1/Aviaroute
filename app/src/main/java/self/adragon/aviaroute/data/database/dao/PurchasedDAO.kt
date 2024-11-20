package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import self.adragon.aviaroute.data.model.Purchased

@Dao
interface PurchasedDAO {
    @Query("SELECT * FROM purchased")
    fun getAllPurchased(): List<Purchased>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchased: Purchased)
}