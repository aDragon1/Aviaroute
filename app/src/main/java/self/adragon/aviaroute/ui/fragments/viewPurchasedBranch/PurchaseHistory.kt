package self.adragon.aviaroute.ui.fragments.viewPurchasedBranch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.adapters.FlightRecyclerViewAdapter
import self.adragon.aviaroute.ui.viewmodels.PurchasedViewModel

class PurchaseHistory(private val onCustomDismiss: () -> Unit) :
    DialogFragment(R.layout.fragment_flight_search_result) {

    private val purchasedViewModel: PurchasedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countElementTextView = view.findViewById<TextView>(R.id.countElementsTextView)
        val flightSearchResultRecyclerView =
            view.findViewById<RecyclerView>(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FlightRecyclerViewAdapter {
            TODO("On item click not yet implemented")
        }
        flightSearchResultRecyclerView.adapter = adapter

        purchasedViewModel.purchasedSearchResultLiveData.observe(viewLifecycleOwner) {
            countElementTextView.text = "Элементов в купленном - ${it.size}"
            adapter.setData(it)
        }
        purchasedViewModel.purchasedLiveData.observe(viewLifecycleOwner) {
            Log.d("mytag", "purchased live data size - ${it?.size ?: -1}")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCustomDismiss()
    }
}