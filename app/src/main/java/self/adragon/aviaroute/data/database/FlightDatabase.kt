package self.adragon.aviaroute.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import self.adragon.aviaroute.data.database.dao.AirportsDAO
import self.adragon.aviaroute.data.database.dao.FlightsDAO
import self.adragon.aviaroute.data.database.dao.PurchasedDAO
import self.adragon.aviaroute.data.database.dao.SearchFlightsDAO
import self.adragon.aviaroute.data.database.dao.SegmentsDAO
import self.adragon.aviaroute.data.model.Airport
import self.adragon.aviaroute.data.model.Flight
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.model.Segment
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.data.model.typeConverters.TToStringConverter

@Database(
    entities = [Airport::class, Segment::class, Flight::class, Purchased::class],
    version = 16,
    exportSchema = true
)
@TypeConverters(TToStringConverter::class)
abstract class FlightsDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: FlightsDatabase? = null
        private const val DBNAME = "FLIGHTS_TABLE.db"

        fun getDatabase(context: Context): FlightsDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightsDatabase::class.java,
                    DBNAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }

        fun deleteDatabase(context: Context) {
            context.deleteDatabase(DBNAME)
            INSTANCE = null
        }
    }

    abstract fun airportsDAO(): AirportsDAO
    abstract fun segmentsDAO(): SegmentsDAO
    abstract fun flightsDAO(): FlightsDAO
    abstract fun searchFlightsDAO(): SearchFlightsDAO
    abstract fun purchasedDAO(): PurchasedDAO
}

