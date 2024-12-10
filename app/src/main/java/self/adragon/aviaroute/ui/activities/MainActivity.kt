package self.adragon.aviaroute.ui.activities

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.data.repo.FlightsRepository
import self.adragon.aviaroute.data.repo.SegmentRepository
import self.adragon.aviaroute.ui.adapters.ViewPagerAdapter
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight.Search
import self.adragon.aviaroute.ui.fragments.viewPurchasedBranch.Profile
import self.adragon.aviaroute.utils.Generator
import kotlin.time.measureTime


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val APP_PREFERENCES = "aviaroute_setting"
    private val APP_PREFERENCES_DATABASE_EXIST = "isDatabaseExist"

    private lateinit var setting: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager2 = findViewById(R.id.mainViewPager2)
        tabLayout = findViewById(R.id.tabLayout)

        val fragments: List<Fragment> =
            listOf(Search(), Profile())

        viewPager2.adapter = ViewPagerAdapter(fragments, this)
        viewPager2.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager2) { tab, i ->
            tab.text = when (i) {
                0 -> "Поиск билета"
                1 -> "Личный кабинет"
                else -> "Ошибка"
            }
        }.attach()
//        requestAllPerms()
        setting = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val isDatabaseExist = setting.getBoolean(APP_PREFERENCES_DATABASE_EXIST, false)
        if (!isDatabaseExist)
            initializeDatabase()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestAllPerms() {
        val perm = Manifest.permission.POST_NOTIFICATIONS
        requestPermissions(arrayOf(perm), 1)
    }

    private fun initializeDatabase() {
        val time = measureTime { populateDatabase() }
        setting.edit().putBoolean(APP_PREFERENCES_DATABASE_EXIST, true).apply()
        Toast.makeText(
            applicationContext, "База данных была успешно создана за $time", Toast.LENGTH_SHORT
        ).show()
        Log.d("mytag", "База данных была успешно создана за $time")
    }

    private fun populateDatabase() {
        FlightsDatabase.deleteDatabase(applicationContext)
        val gen = Generator()

        CoroutineScope(Dispatchers.IO).launch {
            val db = FlightsDatabase.getDatabase(applicationContext)
            val airportsRepo = AirportRepository(db.airportsDAO())
            val segmentsRepo = SegmentRepository(db.segmentsDAO())
            val flightsRepo = FlightsRepository(db.flightsDAO())

            val airports = gen.generateAirports(5)
            val segments = gen.generateSegments(1000)
            val flights = gen.generateFlights(segments, 10000, 4)

            airportsRepo.insertAll(airports)
            segmentsRepo.insertAll(segments)
            flightsRepo.insertAll(flights)

//            gen.generateFlights(segments, 100000, 4)
//                .onEach { flight ->
//                    flightsRepo.insert(flight)
//                    Log.d("mytag", "Flight with key = ${flight.key} inserted")
//                }.flowOn(Dispatchers.IO).collect {}

            withContext(Dispatchers.Main) {
                Log.d("mytag", "airports size - ${airports.size}")
                Log.d("mytag", "segments size - ${segments.size}")
                Log.d("mytag", "flights size - ${flights.size}")
            }
        }
    }
}