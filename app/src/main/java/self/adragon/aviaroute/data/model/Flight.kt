package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "flights")
data class Flight(
    @ColumnInfo(name = "flightIndex")
    val flightIndex: Int,

    @ColumnInfo(name = "segmentIndex")
    val segmentIndex: Int,

    @ColumnInfo(name = "segmentPosition")
    val segmentPosition: Int,

    @ColumnInfo(name = "departureDate")
    @Contextual
    val departureDate: LocalDate,
) {
    @ColumnInfo(name = "key")
    @PrimaryKey(autoGenerate = true)
    var key: Int = 0
}