package self.adragon.aviaroute.ui.fragments.viewPurchasedBranch

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.ui.viewmodels.PurchasedViewModel

class Profile : Fragment(R.layout.profile_layout) {

    private lateinit var departureTextView: TextView
    private lateinit var destinationTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var departureDateTextView: TextView
    private lateinit var destinationDateTextView: TextView
    private lateinit var transferSizeTextView: TextView
    private lateinit var extraArrowRightImageView: ImageView

    private lateinit var flightListItemInclude: View

    private val purchasedViewModel: PurchasedViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val purchaseHistoryButton = view.findViewById<Button>(R.id.purchaseHistoryButton)
        flightListItemInclude = view.findViewById(R.id.flightListItemInclude)

        initViews()

        purchasedViewModel.purchasedSearchResultLiveData.observe(viewLifecycleOwner) { purchased ->

            val isPurchasedEmpty = purchased.isEmpty()

            val (flightIncludeVisibility, flightsAddedTextViewVisibility) = if (isPurchasedEmpty) {
                View.GONE to View.VISIBLE
            } else View.VISIBLE to View.GONE

            flightListItemInclude.visibility = flightIncludeVisibility
            view.findViewById<TextView>(R.id.noFlightIsAddedTextView).visibility =
                flightsAddedTextViewVisibility

            val closest = purchased.minByOrNull { it.destinationDateEpoch }
            setText(closest)
        }

        purchaseHistoryButton.setOnClickListener {
            val purchasedDialogFragment = PurchaseHistory()
            purchasedDialogFragment.show(childFragmentManager, "")
        }
    }

    private fun initViews() {
        departureTextView = flightListItemInclude.findViewById(R.id.departureTextView)
        destinationTextView = flightListItemInclude.findViewById(R.id.destinationTextView)
        priceTextView = flightListItemInclude.findViewById(R.id.flightSummaryTextView)
        departureDateTextView = flightListItemInclude.findViewById(R.id.departureDateTextView)
        destinationDateTextView = flightListItemInclude.findViewById(R.id.destinationDateTextView)
        transferSizeTextView = flightListItemInclude.findViewById(R.id.transferSizeTextView)
        extraArrowRightImageView = flightListItemInclude.findViewById(R.id.extraArrowRightImageView)
    }

    @SuppressLint("SetTextI18n")
    private fun setText(flight: SearchResultFlight?) {
        if (flight == null) return

        departureTextView.text = flight.flightAirportCodes.first()
        destinationTextView.text = flight.flightAirportCodes.last()
        priceTextView.text = "${"%.2f".format(flight.totalPrice)} у.е."
        departureDateTextView.text = flight.departureDateString
        destinationDateTextView.text = flight.destinationDateString

        transferSizeTextView.visibility = View.GONE
        extraArrowRightImageView.visibility = View.GONE

        val codes = flight.flightAirportCodes
        if (codes.size > 2) {
            transferSizeTextView.visibility = View.VISIBLE
            extraArrowRightImageView.visibility = View.VISIBLE

            transferSizeTextView.text =
                "[${if (codes.size == 3) codes[1] else (codes.size - 1).toString()}]"
        }
    }

}