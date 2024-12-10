package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchResult

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchResultParamsSort : Fragment(R.layout.search_result_params_sort) {

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)

        val b1 = createRadioButton("Цене ↑")
        val b2 = createRadioButton("Цене ↓")
        val b3 = createRadioButton("Дате ↑")
        val b4 = createRadioButton("Дате ↓")

        val radioButtons = listOf(b1, b2, b3, b4)
        radioButtons.forEach { radioGroup.addView(it) }

        val index = when (searchResultViewModel.sortOrder.value) {
            SortOrder.PRICE_UP -> 0
            SortOrder.PRICE_DOWN -> 1
            SortOrder.DATE_UP -> 2
            SortOrder.DATE_DOWN -> 3
        }
        radioButtons[index].isChecked = true

        radioGroup.setOnCheckedChangeListener { group: RadioGroup, checkedID: Int ->
            lifecycleScope.launch {
                val button = group.findViewById<RadioButton>(checkedID)
                val order = when (radioButtons.indexOf(button)) {
                    0 -> SortOrder.PRICE_UP
                    1 -> SortOrder.PRICE_DOWN
                    2 -> SortOrder.DATE_UP
                    3 -> SortOrder.DATE_DOWN

                    else -> SortOrder.DATE_DOWN
                }

                searchResultViewModel.setSortOrder(order)
            }
        }
    }

    private fun createRadioButton(text: String): RadioButton {
        val b = RadioButton(context)
        b.text = text
        b.textSize = 16f

        return b
    }
}