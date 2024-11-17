package self.adragon.aviaroute.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.data.repo.FlightsRepository
import self.adragon.aviaroute.data.repo.SegmentRepository
import self.adragon.aviaroute.ui.adapters.ViewPagerAdapter
import self.adragon.aviaroute.ui.fragments.FlightSearchFragment
import self.adragon.aviaroute.utils.Generator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager2 = findViewById(R.id.mainViewPager2)
        tabLayout = findViewById(R.id.tabLayout)

        val fragments: List<Fragment> = listOf(FlightSearchFragment())

        viewPager2.adapter = ViewPagerAdapter(fragments, this)
        viewPager2.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager2) { tab, i ->
            tab.text = when (i) {
                0 -> "Поиск билета"
                1 -> "Личный кабинет"
                else -> "Ошибка"
            }
        }.attach()

//        populateDatabase()
    }

    private fun populateDatabase() {
        FlightsDatabase.deleteDatabase(applicationContext)

        val (airports, segments, flights) =
            Generator()
                .generateDatabaseData(5, 100, 1000, 3)

        val db = FlightsDatabase.getDatabase(applicationContext)

        val airportsRepo = AirportRepository(db.airportsDAO())
        val segmentsRepo = SegmentRepository(db.segmentsDAO())
        val flightsRepo = FlightsRepository(db.flightsDAO())

        CoroutineScope(Dispatchers.IO).launch {
            airports.forEach { airportsRepo.insert(it) }
            segments.forEach { segmentsRepo.insert(it) }
            flights.forEach { flightsRepo.insert(it) }
        }
    }
}