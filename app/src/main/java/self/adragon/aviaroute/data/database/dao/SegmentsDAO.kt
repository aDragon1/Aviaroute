package self.adragon.aviaroute.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import self.adragon.aviaroute.data.model.Segment

@Dao
interface SegmentsDAO {
    @Query("SELECT * FROM segments")
    fun getAllSegments(): List<Segment>

    @Query("SELECT * FROM segments where segmentIndex = :index")
    fun segByIndex(index: Int): Segment

    @Query("SELECT flightTimeEpochSeconds FROM segments where segmentIndex = :index")
    fun getSegmentFlightTime(index: Int): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(segment: Segment)
}