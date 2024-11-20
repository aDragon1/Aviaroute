package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "airports")
data class Airport(
    @ColumnInfo("airportIndex")
    @PrimaryKey
    val airportIndex: Int,

    @ColumnInfo("code")
    val code: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("info")
    val info: String
) {
    override fun toString(): String = "Airport(airportIndex=$airportIndex, code=$code)"
}
