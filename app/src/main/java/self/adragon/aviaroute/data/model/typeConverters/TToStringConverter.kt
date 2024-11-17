package self.adragon.aviaroute.data.model.typeConverters

import androidx.room.TypeConverter

class TToStringConverter {

    @TypeConverter
    fun fromStringToListOfInt(s: String): List<Int> = s.split(", ").map { it.toInt() }

    @TypeConverter
    fun fromListOfTToString(lst: List<String>): String = lst.joinToString { ", " }

    @TypeConverter
    fun fromStringToListOfString(s: String): List<String> = s.split(", ")
}