package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "flights",
    foreignKeys = [
        ForeignKey(
            entity = Segment::class,
            parentColumns = ["segmentIndex"],
            childColumns = ["segmentIndex"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["segmentIndex"])]
)
data class Flight(
    @ColumnInfo("flightIndex")
    val flightIndex: Int,

    @ColumnInfo("segmentIndex")
    val segmentIndex: Int,

    @ColumnInfo("segmentPosition")
    val segmentPosition: Int,

    @ColumnInfo("departureDateEpochSeconds")
    @Contextual
    val departureDateEpochSeconds: Long,
) {
    @ColumnInfo("key")
    @PrimaryKey(autoGenerate = true)
    var key: Int = 0
}