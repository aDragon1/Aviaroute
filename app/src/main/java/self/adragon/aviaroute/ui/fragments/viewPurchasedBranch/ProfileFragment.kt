package self.adragon.aviaroute.ui.fragments.viewPurchasedBranch

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import self.adragon.aviaroute.R

class ProfileFragment : Fragment(R.layout.profile_layout) {

    private var isPurchasedHistoryDialogShown = false

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val purchaseHistoryButton = view.findViewById<Button>(R.id.purchaseHistoryButton)
        val flightListItemInclude = view.findViewById<View>(R.id.flightListItemInclude)

        val priceTextView = flightListItemInclude.findViewById<TextView>(R.id.priceTextView)
        val airlineTextView = flightListItemInclude.findViewById<TextView>(R.id.airlineTextView)

        priceTextView.text = "Hello from include"
        airlineTextView.text = "TODO Add this later"

        purchaseHistoryButton.setOnClickListener {
            if (isPurchasedHistoryDialogShown) return@setOnClickListener

            val purchasedDialogFragment = PurchaseHistory { isPurchasedHistoryDialogShown = false }
            purchasedDialogFragment.show(childFragmentManager, "")
            isPurchasedHistoryDialogShown = true
        }
    }
}