package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchResult

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchResultAdjustDate : Fragment(R.layout.search_result_adjust_date) {

    private lateinit var adjustDateTextView: TextView
    private lateinit var adjustDatePastButton: Button
    private lateinit var adjustDateFutureButton: Button

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adjustDateTextView = view.findViewById(R.id.adjustDateTextView)
        adjustDatePastButton = view.findViewById(R.id.adjustDatePastButton)
        adjustDateFutureButton = view.findViewById(R.id.adjustDateFutureButton)

        val departureIndex = searchResultViewModel.departureAirportIndex
        val destinationIndex = searchResultViewModel.destinationAirportIndex

        CoroutineScope(Dispatchers.IO).launch {
            val (past, future) = searchResultViewModel.getClosestDate()

            withContext(Dispatchers.Main) {
                adjustDateTextView.text = if (past == null && future == null)
                    "Билетов по этому направлению не найдено"
                else "Билетов на выбранную дату не найдено"

                past?.let {
                    val pastStr = LocalDateConverter().fromEpochSecondsStringDate(past)

                    adjustDatePastButton.visibility = View.VISIBLE
                    adjustDatePastButton.text = pastStr
                    adjustDatePastButton.setOnClickListener { _ ->
                        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, it)
                    }
                }

                future?.let {
                    val futureStr = LocalDateConverter().fromEpochSecondsStringDate(future)

                    adjustDateFutureButton.visibility = View.VISIBLE
                    adjustDateFutureButton.text = futureStr
                    adjustDateFutureButton.setOnClickListener { _ ->
                        searchResultViewModel.setSearchResult(departureIndex, destinationIndex, it)
                    }
                }
            }
        }
    }
}