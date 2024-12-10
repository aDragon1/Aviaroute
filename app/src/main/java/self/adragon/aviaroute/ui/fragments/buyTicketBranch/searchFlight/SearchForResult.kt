package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.ui.adapters.FlightSearchForResultRVAdapter

class SearchForResult(private val onItemSelect: (String, Int) -> Unit) :
    DialogFragment(R.layout.flight_search_for_result) {

    private lateinit var backImageButton: ImageButton
    private lateinit var flightSearchForResultRecyclerView: RecyclerView
    private lateinit var airportRepository: AirportRepository
    private var airportNames: List<Pair<String, Int>> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FlightsDatabase.getDatabase(requireContext())
        airportRepository = AirportRepository(db.airportsDAO())

        backImageButton = view.findViewById(R.id.backImageButton)
        flightSearchForResultRecyclerView =
            view.findViewById(R.id.flightSearchForResultRecyclerView)
        flightSearchForResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FlightSearchForResultRVAdapter { name, index ->
            onItemSelect(name, index)
            dismissNow()
        }

        val flightSearchForResultEditText =
            view.findViewById<EditText>(R.id.flightSearchForResultEditText)

        flightSearchForResultEditText.addTextChangedListener {
            val text = flightSearchForResultEditText.text
            val newNames = if (text.isEmpty()) airportNames else
                airportNames.filter { it.first.contains(text, true) }
            adapter.fillData(newNames)
        }

        CoroutineScope(Dispatchers.IO).launch {
            airportNames = airportRepository.getAllAirports()
                .map { "${it.name}, ${it.code}" }
                .mapIndexed { index, s -> s to index }

            withContext(Dispatchers.Main) {
                adapter.fillData(airportNames)
                flightSearchForResultRecyclerView.adapter = adapter
            }
        }
        backImageButton.setOnClickListener { dismiss() }
    }
}