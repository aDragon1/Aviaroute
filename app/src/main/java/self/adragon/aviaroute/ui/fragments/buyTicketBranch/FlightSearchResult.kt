package self.adragon.aviaroute.ui.fragments.buyTicketBranch

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.ui.adapters.FlightRecyclerViewAdapter
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight.FlightSearchSort
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo.SearchFlightInfo
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel


class FlightSearchResult : DialogFragment(R.layout.flight_search_result) {

    private lateinit var backImageButton: ImageButton
    private lateinit var sortImageButton: ImageButton

    private lateinit var flightSearchInfoCodesTextView: TextView

    private lateinit var flightSearchInfoDepartureTextView: TextView
    private lateinit var flightSearchInfoDestinationTextView: TextView
    private lateinit var flightSearchInfDateTextView: TextView
    private lateinit var flightSearchInfoFoundTextView: TextView

    private lateinit var flightSearchResultRecyclerView: RecyclerView

    private lateinit var clicked:SearchResultFlight

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        val adapter = FlightRecyclerViewAdapter { clickedItem ->
            clicked = clickedItem
            startDialog()
        }
        flightSearchResultRecyclerView.adapter = adapter

        val departureIndex = arguments?.getInt("departureIndex", -1) ?: -1
        val destinationIndex = arguments?.getInt("destinationIndex", -1) ?: -1
        val departureDateEpochSeconds = arguments?.getLong("departureDateEpochSeconds", -1L) ?: -1L
        searchResultViewModel.setSearchResult(
            departureIndex, destinationIndex, departureDateEpochSeconds
        )

        val departureAirport = arguments?.getString("departureAirport") ?: ""
        val destinationAirport = arguments?.getString("destinationAirport") ?: ""

        searchResultViewModel.searchResult.observe(viewLifecycleOwner) { lst ->
            val size = lst.size
            val codes = lst.firstOrNull()?.flightAirportCodes ?: emptyList()

            setTextViewsText(
                size, codes, departureAirport, destinationAirport, departureDateEpochSeconds
            )
            adapter.fillData(lst)
            flightSearchResultRecyclerView.adapter = adapter
        }

        backImageButton.setOnClickListener {
            dismiss()
        }
        sortImageButton.setOnClickListener {
            FlightSearchSort().show(childFragmentManager, "")
        }
    }

    private fun initViews(view: View) {
        flightSearchInfoCodesTextView = view.findViewById(R.id.flightSearchInfoCodesTextView)

        flightSearchInfoDepartureTextView =
            view.findViewById(R.id.flightSearchInfoDepartureTextView)
        flightSearchInfoDestinationTextView =
            view.findViewById(R.id.flightSearchInfoDestinationTextView)
        flightSearchInfDateTextView = view.findViewById(R.id.flightSearchInfDateTextView)
        flightSearchInfoFoundTextView =
            view.findViewById(R.id.flightSearchInfoFoundTextView)

        backImageButton = view.findViewById(R.id.backImageButton)
        sortImageButton = view.findViewById(R.id.sortImageButton)

        flightSearchResultRecyclerView = view.findViewById(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setTextViewsText(
        size: Int, codes: List<String>,
        departureAirport: String, destinationAirport: String,
        epochSeconds: Long
    ) {
        val codesMessage =
            if (codes.isEmpty()) "" else "${codes.firstOrNull()} → ${codes.lastOrNull()}"
        val departureMessage = "Откуда: $departureAirport"
        val destinationMessage = "Куда: $destinationAirport"
        val dateMessage = "Дата вылета: " +
                LocalDateConverter().fromEpochSecondsStringDate(epochSeconds)
        val foundMessage = "\nНайдено билетов: $size"

        flightSearchInfoCodesTextView.text = codesMessage

        flightSearchInfoDepartureTextView.text = departureMessage
        flightSearchInfoDestinationTextView.text = destinationMessage
        flightSearchInfDateTextView.text = dateMessage
        flightSearchInfoFoundTextView.text = foundMessage
    }

    private fun startDialog() {
        val frag = SearchFlightInfo(clicked)
        frag.show(childFragmentManager, "SEARCH_INFO_DIALOG_TAG")
    }
}