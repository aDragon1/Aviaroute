package self.adragon.aviaroute.data.model.typeConverters

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LocalDateConverter {
//    private fun fromDateToString(date: LocalDate): String {
//        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
//        return date.format(formatter)
//    }

//    fun fromEpochDayToStringDate(epochSeconds: Long) =
//        fromDateToString(fromEpochDayToLocalDate(epochSeconds))

    fun fromEpochDayToStringDate(epochSeconds: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

        val instantOfEpoch = Instant.ofEpochSecond(epochSeconds)
        val instantAsLocalDateTime = instantOfEpoch.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return instantAsLocalDateTime.format(formatter)
    }
}
