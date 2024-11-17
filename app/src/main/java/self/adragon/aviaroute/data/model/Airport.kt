package self.adragon.aviaroute.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "airports")
data class Airport(
    @ColumnInfo(name = "airportIndex")
    @PrimaryKey
    val airportIndex: Int,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "info")
    val info: String
) {
    override fun toString(): String = "Airport(airportIndex=$airportIndex, code=$code)"
}
