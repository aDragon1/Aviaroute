package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.PickDate
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.FlightSearchResult

class FlightSearch : Fragment(R.layout.flight_search), OnClickListener {

    private lateinit var swapButton: ImageButton
    private lateinit var searchButton: Button

    private lateinit var departureAirportInclude: View
    private lateinit var destinationAirportInclude: View

    private lateinit var departureIncludeTextView: TextView
    private lateinit var destinationIncludeTextView: TextView

    private lateinit var departureIncludeClear: ImageButton
    private lateinit var destinationIncludeClear: ImageButton

    private var departureIndex = -1
    private var destinationIndex = -1

    private var departureDateEpochSeconds = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        departureAirportInclude = view.findViewById(R.id.departureAirportInclude)
        destinationAirportInclude = view.findViewById(R.id.destinationAirportInclude)

        departureIncludeTextView = departureAirportInclude.findViewById(R.id.tv_search)
        destinationIncludeTextView = destinationAirportInclude.findViewById(R.id.tv_search)

        departureIncludeClear = departureAirportInclude.findViewById(R.id.iv_icon_clear)
        destinationIncludeClear = destinationAirportInclude.findViewById(R.id.iv_icon_clear)

        swapButton = view.findViewById(R.id.swapButton)
        searchButton = view.findViewById(R.id.searchButton)

        swapButton.setOnClickListener(this)
        searchButton.setOnClickListener(this)

        departureIncludeTextView.hint = "Откуда"
        destinationIncludeTextView.hint = "Куда"

        departureAirportInclude.setOnClickListener {
            val fragForResult = FlightSearchForResult { name, index ->
                departureIncludeTextView.text = name
                departureIndex = index + 1
            }
            fragForResult.show(childFragmentManager, "")
        }
        destinationAirportInclude.setOnClickListener {
            val fragForResult = FlightSearchForResult { name, index ->
                destinationIncludeTextView.text = name
                destinationIndex = index + 1
            }
            fragForResult.show(childFragmentManager, "")
        }

        departureIncludeClear.setOnClickListener {
            departureIncludeTextView.text = ""
            departureIndex = -1
        }
        destinationIncludeClear.setOnClickListener {
            destinationIncludeTextView.text = ""
            destinationIndex = -1
        }

        val pickDate = PickDate { epochSeconds ->
            departureDateEpochSeconds = epochSeconds
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.datePickerContainer, pickDate)
            .commit()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.swapButton -> {
                val depQ = departureIncludeTextView.text
                val destQ = destinationIncludeTextView.text

                departureIncludeTextView.text = destQ
                destinationIncludeTextView.text = depQ

                val tempIndex = destinationIndex
                destinationIndex = departureIndex
                departureIndex = tempIndex
            }

            R.id.searchButton -> {
                if (!handleValidateError()) return

                val arg = Bundle().apply {
                    putInt("departureIndex", departureIndex)
                    putString("departureAirport", departureIncludeTextView.text.toString())

                    putInt("destinationIndex", destinationIndex)
                    putString("destinationAirport", destinationIncludeTextView.text.toString())

                    putLong("departureDateEpochSeconds", departureDateEpochSeconds)
                }
                val searchResultFragment = FlightSearchResult().apply { arguments = arg }
                searchResultFragment.show(childFragmentManager, "cool tag")
            }
        }
    }

    private fun handleValidateError(): Boolean {
//        return true // uncomment for test

        val isValidDeparture = isValidSearch(departureIndex)
        val isValidDestination = isValidSearch(destinationIndex)
        val isValidDate = departureDateEpochSeconds != -1L

        val s = when {
            !isValidDeparture && !isValidDestination -> "Выберите аэропорт отправления и аэропорт назначения"
            !isValidDeparture && isValidDestination -> "Выберите аэропорт отправления"
            isValidDeparture && !isValidDestination -> "Выберите аэропорт назначения"
            !isValidDate -> "Выберите дату отправления"

            else -> ""
        }

        if (s.isNotEmpty()) {
            Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isValidSearch(index: Int): Boolean {
        return index != -1
    }
}
