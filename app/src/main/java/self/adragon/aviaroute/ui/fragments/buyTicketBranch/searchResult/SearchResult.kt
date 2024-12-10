package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchResult

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.ui.adapters.FlightRecyclerViewAdapter
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo.SearchedFlightInfo
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchResult : DialogFragment(R.layout.flight_search_result) {

    private lateinit var backImageButton: ImageButton
    private lateinit var paramsImgButton: ImageButton

    private lateinit var flightSearchInfoCodesTextView: TextView

    private lateinit var flightSearchInfoDepartureTextView: TextView
    private lateinit var flightSearchInfoDestinationTextView: TextView
    private lateinit var flightSearchInfDateTextView: TextView
    private lateinit var flightSearchInfoFoundTextView: TextView

    private lateinit var flightSearchResultRecyclerView: RecyclerView

    private lateinit var clicked: SearchResultFlight
    private val adjustDateTag = "adjustDateTag"
    private val searchInfoTag = "searchInfoTag"

    private val paramsDialog = SearchResultParams()
    private lateinit var searchedFlightInfoDialog: SearchedFlightInfo
    private val adjustDate = SearchResultAdjustDate()

    private val localDateConverter = LocalDateConverter()

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        val adapter = FlightRecyclerViewAdapter { clickedItem ->
            clicked = clickedItem

            if (::searchedFlightInfoDialog.isInitialized && searchedFlightInfoDialog.isShown)
                return@FlightRecyclerViewAdapter
            searchedFlightInfoDialog = SearchedFlightInfo(clicked)
            searchedFlightInfoDialog.show(childFragmentManager, searchInfoTag)
        }
        flightSearchResultRecyclerView.adapter = adapter

        val departureAirport = arguments?.getString("departureAirport") ?: ""
        val destinationAirport = arguments?.getString("destinationAirport") ?: ""
        setSearchResults()


        // TODO: If params slider value are too bruh (flight time is too small or whatever) it doesn't trigger the flow. It just stay empty
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchResultViewModel.searchResultFlightsFlow.collect { flights ->
                    val epochSeconds = searchResultViewModel.departureDateEpochSeconds
                    val size = flights.size
                    Log.d("mytag", "Flow list size = ${flights.size}")
                    childFragmentManager.beginTransaction().remove(adjustDate).commit()

                    withContext(Dispatchers.Main) {
                        setTextViewsText(size, departureAirport, destinationAirport, epochSeconds)

                        adapter.fillData(flights)
                        flightSearchResultRecyclerView.adapter = adapter

                        if (size == 0)
                            childFragmentManager.beginTransaction()
                                .replace(R.id.adjustDateContainer, adjustDate, adjustDateTag)
                                .commit()
                    }
                }
            }
        }

        backImageButton.setOnClickListener {
            dismiss()
        }
        paramsImgButton.setOnClickListener {
            if (!paramsDialog.isShown)
                paramsDialog.show(childFragmentManager, "")
        }
    }

    private fun setSearchResults() {
        val departureIndex = arguments?.getInt("departureIndex", -1) ?: -1
        val destinationIndex = arguments?.getInt("destinationIndex", -1) ?: -1
        val depEpochSeconds = arguments?.getLong("departureDateEpochSeconds", -1L) ?: -1L

        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, depEpochSeconds)
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
        paramsImgButton = view.findViewById(R.id.paramsImgButton)

        flightSearchResultRecyclerView = view.findViewById(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setTextViewsText(
        size: Int, departureAirport: String,
        destinationAirport: String, epochSeconds: Long
    ) {
        val codesMessage = if (departureAirport.isNotEmpty()) {
            val departureCode = departureAirport.split(", ")[1]
            val destinationCode = destinationAirport.split(", ")[1]
            "$departureCode → $destinationCode"
        } else ""
        val departureMessage = "Откуда: $departureAirport"
        val destinationMessage = "Куда: $destinationAirport"
        val dateMessage = "Дата вылета: " +
                localDateConverter.fromEpochSecondsStringDate(epochSeconds)
        val foundMessage = "\nНайдено билетов: $size"

        flightSearchInfoCodesTextView.text = codesMessage

        flightSearchInfoDepartureTextView.text = departureMessage
        flightSearchInfoDestinationTextView.text = destinationMessage
        flightSearchInfDateTextView.text = dateMessage
        flightSearchInfoFoundTextView.text = foundMessage
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        searchResultViewModel.clear()
    }
}