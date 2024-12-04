package self.adragon.aviaroute.ui.fragments.viewPurchasedBranch

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.ui.adapters.PurchasedRVAdapter
import self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo.SearchFlightInfo
import self.adragon.aviaroute.ui.viewmodels.PurchasedViewModel

class PurchaseHistory : DialogFragment(R.layout.purchase_history) {

    private lateinit var backImageButton: ImageButton
    private lateinit var countElementTextView: TextView
    private lateinit var flightSearchResultRecyclerView: RecyclerView

    private val purchasedViewModel: PurchasedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.FullScreenDialogTheme)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backImageButton = view.findViewById(R.id.backImageButton)
        countElementTextView = view.findViewById(R.id.flightSearchInfoFoundTextView)
        flightSearchResultRecyclerView =
            view.findViewById(R.id.flightSearchResultRecyclerView)
        flightSearchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PurchasedRVAdapter { clicked ->
            val frag = SearchFlightInfo(clicked)
            frag.show(childFragmentManager, "SEARCH_INFO_DIALOG_TAG")
        }
        flightSearchResultRecyclerView.adapter = adapter

        purchasedViewModel.purchasedSearchResultLiveData.observe(viewLifecycleOwner) {
            countElementTextView.text = "Элементов в купленном - ${it.size}"
            adapter.fillData(it)
        }

        backImageButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
}