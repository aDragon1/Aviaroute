package self.adragon.aviaroute.ui.fragments.dialogFragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.repo.PurchasedRepository
import self.adragon.aviaroute.ui.fragments.SearchFlightInfoFragmentItem
import self.adragon.aviaroute.ui.viewmodels.SearchResulViewModel

class SearchFlightInfoFragment(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.search_result_flight_info) {

    private val searchResultViewModel: SearchResulViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()

        val mp = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(mp, mp)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container =
            view.findViewById<LinearLayout>(R.id.searchResultFlightInfoFragmentContainer)
        val buyButton = view.findViewById<Button>(R.id.buyButton)

        searchResultViewModel.clickedItem.observe(viewLifecycleOwner) { clicked ->
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
            buyButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = FlightsDatabase.getDatabase(requireContext())
                    val purchasedRepo = PurchasedRepository(db.purchasedDAO())

                    val purchased = Purchased(flightIndex = clicked.flightIndex)
                    purchasedRepo.insert(purchased)

                    withContext(Dispatchers.Main) {
                        Log.d("mytag", "trying to insert - $purchased")

                        searchResultViewModel.clickedItem.removeObservers(viewLifecycleOwner)
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onCustomDismiss()
    }
}