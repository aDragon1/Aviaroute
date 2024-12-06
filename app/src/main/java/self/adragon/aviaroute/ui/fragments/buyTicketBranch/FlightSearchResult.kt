package self.adragon.aviaroute.ui.fragments.buyTicketBranch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private lateinit var dialogInclude: View
    private lateinit var emptyListMessageTextView: TextView
    private lateinit var emptyListPastButton: Button
    private lateinit var emptyListFutureButton: Button

    private lateinit var clicked: SearchResultFlight

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
            val frag = SearchFlightInfo(clicked)
            frag.show(childFragmentManager, "SEARCH_INFO_DIALOG_TAG")
        }
        flightSearchResultRecyclerView.adapter = adapter

        val departureIndex = arguments?.getInt("departureIndex", -1) ?: -1
        val destinationIndex = arguments?.getInt("destinationIndex", -1) ?: -1
        val depEpochSeconds = arguments?.getLong("departureDateEpochSeconds", -1L) ?: -1L

        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, depEpochSeconds)

        val departureAirport = arguments?.getString("departureAirport") ?: ""
        val destinationAirport = arguments?.getString("destinationAirport") ?: ""

        searchResultViewModel.searchResult.observe(viewLifecycleOwner) { lst ->
            val epochSeconds = searchResultViewModel.departureDateEpochSeconds
            val size = lst.size

            dialogInclude.visibility = View.GONE
            setTextViewsText(size, departureAirport, destinationAirport, epochSeconds)

            adapter.fillData(lst)
            flightSearchResultRecyclerView.adapter = adapter

            if (size == 0) {
                handleEmptyList(departureIndex, destinationIndex, epochSeconds)
                dialogInclude.visibility = View.VISIBLE
            }
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

        initEmptyListVies(view)
    }

    private fun initEmptyListVies(view: View) {
        dialogInclude = view.findViewById(R.id.dialogInclude)
        emptyListMessageTextView =
            dialogInclude.findViewById(R.id.emptyListMessageTextView)
        emptyListPastButton =
            dialogInclude.findViewById(R.id.emptyListPastButton)
        emptyListFutureButton =
            dialogInclude.findViewById(R.id.emptyListFutureButton)
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

    private fun getEmptyListMessage(past: Long?, future: Long?) =
        when {
            past == null && future == null -> "Билетов по этому направлению не найдено"
            past == null || future == null -> "Билетов на выбранную дату не найдено"
            else -> "Билетов на выбранную дату не найдено"
        }


    private fun handleEmptyList(
        departureIndex: Int,
        destinationIndex: Int,
        epochSeconds: Long
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val (past, future) = searchResultViewModel.getClosestDate(
                departureIndex, destinationIndex, epochSeconds
            )

            val message = getEmptyListMessage(past, future)
            withContext(Dispatchers.Main) {
                updateEmptyListUI(message, past, future, departureIndex, destinationIndex)
            }
        }
    }


    private fun updateEmptyListUI(
        message: String,
        past: Long?,
        future: Long?,
        departureIndex: Int,
        destinationIndex: Int
    ) {
        emptyListMessageTextView.text = message

        val converter = LocalDateConverter()
        val pastString = converter.fromEpochSecondsStringDate(past)
        val futureString = converter.fromEpochSecondsStringDate(future)

        emptyListPastButton.apply {
            visibility = if (past == null) View.GONE else View.VISIBLE
            text = pastString
            past?.let {
                setOnClickListener { _ ->
                    searchResultViewModel.setSearchResult(departureIndex, destinationIndex, it)
                }
            }
        }

        emptyListFutureButton.apply {
            visibility = if (future == null) View.GONE else View.VISIBLE
            text = futureString
            setOnClickListener {
                future?.let {
                    searchResultViewModel.setSearchResult(departureIndex, destinationIndex, it)
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        searchResultViewModel.clear()
    }
}