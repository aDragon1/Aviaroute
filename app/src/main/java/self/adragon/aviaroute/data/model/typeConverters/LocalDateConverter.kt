package self.adragon.aviaroute.data.model.typeConverters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    @TypeConverter
    fun fromEpochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun fromDateToEpochDay(date: LocalDate) = date.toEpochDay()

    fun fromDateToString(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return date.format(formatter)
    }
}
