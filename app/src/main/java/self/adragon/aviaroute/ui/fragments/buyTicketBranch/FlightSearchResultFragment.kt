package self.adragon.aviaroute.ui.fragments.buyTicketBranch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.adapters.FlightRecyclerViewAdapter
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo.SearchFlightInfoFragment
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel


class FlightSearchResultFragment(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.fragment_flight_search_result) {

    private lateinit var flightSearchResultRecyclerView: RecyclerView
    private var isSearchInfoDialogShown = false

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countElementsTextView = view.findViewById<TextView>(R.id.countElementsTextView)
        flightSearchResultRecyclerView = view.findViewById(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FlightRecyclerViewAdapter { clickedItem ->
            searchResultViewModel.setClickedItem(clickedItem)
            startDialog()
        }
        flightSearchResultRecyclerView.adapter = adapter

        val errorValue = -1
        val departureIndex = arguments?.getInt("departureIndex", errorValue) ?: errorValue
        val destinationIndex = arguments?.getInt("destinationIndex", errorValue) ?: errorValue
        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, errorValue)

        searchResultViewModel.searchResult.observe(viewLifecycleOwner) {
            countElementsTextView.text = "Элементов в списке: ${it.size}"
            adapter.setData(it)
        }
    }

    private fun startDialog() {
        if (isSearchInfoDialogShown) return

        val frag = SearchFlightInfoFragment {
            isSearchInfoDialogShown = false
        }

        frag.show(childFragmentManager, "SEARCH_INFO_DIALOG_TAG")
        isSearchInfoDialogShown = true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        searchResultViewModel.clickedItem.value = null
        onCustomDismiss()
    }
}