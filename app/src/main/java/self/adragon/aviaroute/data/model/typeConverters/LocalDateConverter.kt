package self.adragon.aviaroute.data.model.typeConverters

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    fun fromEpochSecondsStringDate(epochSeconds: Long?): String =
        fromEpochSecondsStringDateTime(epochSeconds).split(',').first()

    fun fromEpochSecondsStringDateTime(epochSeconds: Long?): String {
        if (epochSeconds == null) return ""
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

        val instantOfEpoch = Instant.ofEpochSecond(epochSeconds)
        val instantAsLocalDateTime =
            instantOfEpoch.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return instantAsLocalDateTime.format(formatter)
    }

    fun fromEpochSecondToTimeString(epochSeconds: Long?): String {
        if (epochSeconds == null) return ""

        val secondsInMinute = 60
        val secondsInHour = secondsInMinute * 60
        val secondsInDay = secondsInHour * 24

        val hourInDay = 24
        val minuteInHour = 60

        val days = epochSeconds.div(secondsInDay)
        val hours = epochSeconds.div(secondsInHour) % hourInDay
        val minutes = epochSeconds.div(secondsInMinute) % minuteInHour
        val seconds = epochSeconds % secondsInMinute

        val dayStr = if (days > 0) "$days д., " else ""
        val hoursStr = if (hours > 0) "$hours ч., " else ""
        val minutesStr = if (minutes > 0) "$minutes м., " else ""
        val secondsStr = if (seconds > 0) "$seconds с." else ""

        return dayStr + hoursStr + minutesStr + secondsStr
    }

    fun getDayRange(epochSeconds: Long): Pair<Long, Long> {
        val offset = ZoneId.systemDefault()

        val date = Instant.ofEpochSecond(epochSeconds).atZone(offset).toLocalDate()
        val startOfDay = date.atStartOfDay(offset).toEpochSecond()
        val endOfDay = startOfDay + 86400 - 1

        return startOfDay to endOfDay
    }
}
