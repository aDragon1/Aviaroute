package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchResult

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchResultParams : DialogFragment(R.layout.search_result_params) {
    var isShown = false

    private lateinit var backImageButton: ImageButton

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isShown = true

        backImageButton = view.findViewById(R.id.backImageButton)

        val sortFrag = SearchResultParamsSort()

        childFragmentManager.beginTransaction()
            .replace(R.id.sortContainer, sortFrag)
            .commit()

        CoroutineScope(Dispatchers.IO).launch {

            val (priceRange, curPriceRange) = searchResultViewModel.getPriceRange()
            val (flightTimeRange, curFlightTimeRange) = searchResultViewModel.getFlightTimeRange()

            withContext(Dispatchers.Main) {

                val priceRangeSliderFrag =
                    SearchResultParamsRangeSlider("Цена:", priceRange, curPriceRange, true)
                val dateRangeSliderFrag =
                    SearchResultParamsRangeSlider("Время в пути:",flightTimeRange,  curFlightTimeRange, false)

                childFragmentManager.beginTransaction()
                    .add(R.id.priceRangeSliderContainer, priceRangeSliderFrag)
                    .add(R.id.priceRangeSliderContainer, dateRangeSliderFrag)
                    .commit()
            }
        }
        backImageButton.setOnClickListener { dismiss() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        isShown = false
    }
}