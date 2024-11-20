package self.adragon.aviaroute.ui.fragments.dialogFragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.adapters.FlightRecyclerViewAdapter
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel


class FlightSearchResultFragment(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.fragment_flight_search_result) {
    private lateinit var flightSearchResultRecyclerView: RecyclerView
    private var isSearchInfoDialogShown = false

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()

        val mp = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(mp, mp)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countElementsTextView = view.findViewById<TextView>(R.id.countElementsTextView)
        flightSearchResultRecyclerView = view.findViewById(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = FlightRecyclerViewAdapter { clickedItem ->
            searchResultViewModel.setClickedItem(clickedItem)

            startDialog()
        }
        flightSearchResultRecyclerView.adapter = adapter

        val errorValue = -1
        val departureIndex = arguments?.getInt("departureIndex", errorValue) ?: errorValue
        val destinationIndex = arguments?.getInt("destinationIndex", errorValue) ?: errorValue

        Log.d(
            "mytag",
            "departureIndex - $departureIndex, destinationIndex - $destinationIndex"
        )
        searchResultViewModel.searchResult.observe(viewLifecycleOwner) {
            countElementsTextView.text = "Элементов в списке: ${it.size}"
            Log.d("mytag", "found ${it.size} entry's")
            it.forEach { res ->
                val index = res.flightIndex
                val codes = res.flightAirportCodes
                Log.d("mytag", "flights index - $index : $codes")
            }
            adapter.setData(it)
        }

        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, errorValue)
    }

    private fun startDialog() {
        if (isSearchInfoDialogShown) return

        val frag = SearchFlightInfoFragment {
            isSearchInfoDialogShown = false

            searchResultViewModel.clickedItem.value = null
            Log.d("mytag", "clicked item - ${searchResultViewModel.clickedItem.value}")
        }

        frag.show(childFragmentManager, "")
        isSearchInfoDialogShown = true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        Log.d("mytag", "${this::class.java} dismiss")
        onCustomDismiss()
    }
}