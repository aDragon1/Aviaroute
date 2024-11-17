package self.adragon.aviaroute.data.model.searchResult

import androidx.room.ColumnInfo
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import java.time.LocalDate

@Serializable
data class SearchResultFlight(
    val flightIndex: Int,
    val departureDateEpoch: Long,
    val totalPrice: Double,
    @Contextual
    val flightSegments: List<Int>,
    val flightAirportCodes: List<String>,
) {
//    @Contextual
//    val departureDateString: LocalDate
//    @Contextual
//    val destinationDateString: LocalDate
//    val destinationDateEpoch: Long = 1
//
//    val timeInFlightEpoch = destinationDateEpoch - departureDateEpoch
//
//    init {
//        val localDateConverter = LocalDateConverter()
//        departureDateString = localDateConverter.fromEpochDayToLocalDate(departureDateEpoch)
//        destinationDateString = localDateConverter.fromEpochDayToLocalDate(destinationDateEpoch)
//    }
}