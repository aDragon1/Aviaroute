package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.ui.viewmodels.PurchasedViewModel
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchFlightInfoFragment(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.search_result_flight_info) {

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()
    private val purchasedViewModel: PurchasedViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialogTheme)
    }

//    override fun onStart() {
//        super.onStart()
//
//        val mp = ViewGroup.LayoutParams.MATCH_PARENT
//        dialog?.window?.setLayout(mp, mp)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container =
            view.findViewById<LinearLayout>(R.id.searchResultFlightInfoFragmentContainer)
        val buyButton = view.findViewById<Button>(R.id.buyButton)

        var clickedFlightIndex = -1
        var departure = ""
        var destination = ""
        searchResultViewModel.clickedItem.observe(viewLifecycleOwner) { clicked ->
            clickedFlightIndex = clicked.flightIndex
            departure = clicked.flightAirportCodes.first()
            destination = clicked.flightAirportCodes.last()

            clicked.flightAirportCodes
            clicked.flightSegments.forEachIndexed { i, segmentIndex ->
                // All segments from first to current
                val segmentsIndexesIncludeCurrent = clicked.flightSegments.slice(0..i).toIntArray()

                val args = Bundle()
                args.putLong("departureTimeEpoch", clicked.departureDateEpoch)
                args.putIntArray("segmentsIndexesIncludeCurrent", segmentsIndexesIncludeCurrent)

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<SearchFlightInfoFragmentItem>(
                        container.id, "InfoFragmentItem_$segmentIndex", args
                    )
                }
            }
        }

        buyButton.setOnClickListener {
            if (clickedFlightIndex == -1) return@setOnClickListener

            val purchased = Purchased(flightIndex = clickedFlightIndex)
            purchasedViewModel.insert(purchased)

            val message = "Билет из $departure в $destination был куплен"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            searchResultViewModel.clickedItem.removeObservers(viewLifecycleOwner)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onCustomDismiss()
    }
}