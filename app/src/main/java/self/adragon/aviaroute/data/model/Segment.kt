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
            childColumns = ["departureIndex"],
            onDelete = ForeignKey.CASCADE
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
    @ColumnInfo("segmentIndex")
    @PrimaryKey
    val segmentIndex: Int,

    @ColumnInfo("departureIndex", index = true)
    val departureIndex: Int,

    @ColumnInfo("destinationIndex", index = true)
    val destinationIndex: Int,

    @ColumnInfo(name = "flightTimeEpochSeconds")
    val flightTimeEpochSeconds: Long,

    @ColumnInfo("price")
    val price: Double,

    @ColumnInfo("flightNumber")
    val flightNumber: String
)