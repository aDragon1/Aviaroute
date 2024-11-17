package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(
    tableName = "segments",
    foreignKeys = [
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["airportIndex"],
            childColumns = ["departureIndex"]
        ),
        ForeignKey(
            entity = Airport::class,
            parentColumns = ["airportIndex"],
            childColumns = ["destinationIndex"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class Segment(
    @ColumnInfo(name = "segmentIndex")
    @PrimaryKey
    val segmentIndex: Int,

    @ColumnInfo(name = "departureIndex", index = true)
    val departureIndex: Int,

    @ColumnInfo(name = "destinationIndex", index = true)
    val destinationIndex: Int,

    @ColumnInfo(name = "flightTime")
    val flightTime: Long,

    @ColumnInfo(name = "price")
    val price: Double
)