package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlight

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.enums.SortOrder
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class FlightSearchSort : DialogFragment(R.layout.search_params_sort) {

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val backImageButton: ImageButton = view.findViewById(R.id.backImageButton)

        val b1 = createRadioButton("По умолчанию")
        val b2 = createRadioButton("Цене ↑")
        val b3 = createRadioButton("Цене ↓")
        val b4 = createRadioButton("Дате ↑")
        val b5 = createRadioButton("Дате ↓")

        val radioButtons = listOf(b1, b2, b3, b4, b5)
        radioButtons.forEach { radioGroup.addView(it) }

        val index = when (searchResultViewModel.sortOrder.value) {
            SortOrder.PRICE_UP -> 0
            SortOrder.PRICE_DOWN -> 1
            SortOrder.DATE_UP -> 2
            SortOrder.DATE_DOWN -> 3

            else -> 3
        }
        radioButtons[index].isChecked = true

        radioGroup.setOnCheckedChangeListener { group: RadioGroup, checkedID: Int ->
            val button = group.findViewById<RadioButton>(checkedID)
            val order = when (radioButtons.indexOf(button)) {
                0 -> SortOrder.PRICE_UP
                1 -> SortOrder.PRICE_DOWN
                2 -> SortOrder.DATE_UP
                3 -> SortOrder.DATE_DOWN

                else -> SortOrder.DATE_DOWN
            }

            val result = searchResultViewModel.setSortOrder(order)

            if (result)
                Toast.makeText(requireContext(), "Сортировка изменена", Toast.LENGTH_SHORT).show()
        }

        backImageButton.setOnClickListener {
            dismiss()
        }
    }

    private fun createRadioButton(text: String): RadioButton {
        val b = RadioButton(context)
        b.text = text
        b.textSize = 16f

        return b
    }
}