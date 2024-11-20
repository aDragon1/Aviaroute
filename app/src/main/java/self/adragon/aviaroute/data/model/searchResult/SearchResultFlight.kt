package self.adragon.aviaroute.data.model.searchResult

import androidx.room.Ignore
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter

@Serializable
data class SearchResultFlight(
    val flightIndex: Int,
    val departureDateEpoch: Long,
    val destinationDateEpoch: Long,
    val totalPrice: Double,
    @Contextual
    val flightSegments: List<Int>,
    val flightAirportCodes: List<String>,
) {
    @Ignore
    var departureDateString: String

    @Ignore
    var destinationDateString: String

    init {
        val converter = LocalDateConverter()

        departureDateString = converter.fromEpochDayToStringDate(departureDateEpoch)
        destinationDateString = converter.fromEpochDayToStringDate(destinationDateEpoch)
    }
}