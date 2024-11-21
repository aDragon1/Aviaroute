package self.adragon.aviaroute.ui.fragments.buyTicketBranch

import android.app.SearchManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.utils.SearchViewHelper

class FlightSearchFragment : Fragment(R.layout.fragment_flight_search), OnClickListener {
    private lateinit var swapButton: ImageButton
    private lateinit var searchButton: Button

    private lateinit var departureAirportSearchView: SearchView
    private lateinit var destinationAirportSearchView: SearchView

    private var isSearchResultDialogShown = false
    private var departureIndex: Int = -1
    private var destinationIndex: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departureAirportSearchView = view.findViewById(R.id.departureAirportSearchView)
        destinationAirportSearchView = view.findViewById(R.id.destinationAirportSearchView)

        swapButton = view.findViewById(R.id.swapButton)
        searchButton = view.findViewById(R.id.searchButton)

        swapButton.setOnClickListener(this)
        searchButton.setOnClickListener(this)

        val tempAirportsTextView: TextView = view.findViewById(R.id.tempAirportsTextView)

        val db = FlightsDatabase.getDatabase(requireContext())
        val airportsRepo = AirportRepository(db.airportsDAO())

        CoroutineScope(Dispatchers.IO).launch {
            val airportNames = airportsRepo.getAllAirports().map { "${it.name}, ${it.code}" }

            withContext(Dispatchers.Main) {
                tempAirportsTextView.text = airportNames.mapIndexed { i, name ->
                    "${i + 1}) $name"
                }.joinToString("\n")

                setSearchViewStuff(airportNames)
            }
        }
    }

    private fun setSearchViewStuff(airportNames: List<String>) {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.searchItemTextView)
        val suggestionAdapter = SimpleCursorAdapter(
            requireContext(), R.layout.search_item,
            null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        departureAirportSearchView.suggestionsAdapter = suggestionAdapter
        destinationAirportSearchView.suggestionsAdapter = suggestionAdapter

        val helper = SearchViewHelper(airportNames.mapIndexed { index, s -> s to index })
        helper.setSearchViewListener(departureAirportSearchView) { index ->
            departureIndex = index + 1
            Log.d("mytag", "departureIndex - $departureIndex")
        }
        helper.setSearchViewListener(destinationAirportSearchView) { index ->
            destinationIndex = index + 1
            Log.d("mytag", "destinationIndex - $destinationIndex")
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.swapButton -> {
                val depQ = departureAirportSearchView.query
                val destQ = destinationAirportSearchView.query

                val tempIndex = destinationIndex
                destinationIndex = departureIndex
                departureIndex = tempIndex

                departureAirportSearchView.setQuery(destQ, false)
                destinationAirportSearchView.setQuery(depQ, false)
            }

            R.id.searchButton -> {

//                if (departureIndex == -1 && destinationIndex == -1) {
//                    val s = "Выберите аэропорт отправления или аэропорт назначения"
//                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
//                    return
//                }
                if (!isSearchResultDialogShown) {
                    val searchResultFrag =
                        FlightSearchResultFragment { isSearchResultDialogShown = false }
                    val bundle = Bundle()

                    bundle.putInt("departureIndex", departureIndex)
                    bundle.putInt("destinationIndex", destinationIndex)
                    searchResultFrag.arguments = bundle
//
//                    val transaction = childFragmentManager.beginTransaction()
//                    transaction.setCustomAnimations(
//                        R.anim.slide_in_up,
//                        R.anim.slide_out_down
//                    )
//                    transaction.addToBackStack(null)
//                    transaction.add(searchResultFrag, "")
//                    transaction.commit()

                    searchResultFrag.show(childFragmentManager, "")
                    isSearchResultDialogShown = true
                }
            }
        }
    }
}