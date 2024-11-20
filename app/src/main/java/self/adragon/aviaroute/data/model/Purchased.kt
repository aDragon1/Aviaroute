package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "purchased")
data class Purchased(
    @PrimaryKey(autoGenerate = true)
    var purchasedIndex: Int = 0,

    @ColumnInfo("flightIndex")
    val flightIndex: Int,
)