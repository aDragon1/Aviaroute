package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.Purchased
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.ui.viewmodels.PurchasedViewModel
import kotlin.math.roundToInt

class SearchFlightInfo(private val flight: SearchResultFlight) :
    DialogFragment(R.layout.search_result_flight_info) {

    private val purchasedViewModel: PurchasedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container =
            view.findViewById<LinearLayout>(R.id.searchResultFlightInfoFragmentContainer)
        val buyButton = view.findViewById<Button>(R.id.buyButton)
        val totalFlightTimeTextView = view.findViewById<TextView>(R.id.totalFlightTimeTextView)
        val totalPriceTextView = view.findViewById<TextView>(R.id.totalPriceTextView)

        buyButton.text = "Выбрать за ${flight.totalPrice.round()} у.е"

        flight.flightAirportCodes
        flight.flightSegments.forEachIndexed { i, _ ->
            // All segments from first to current
            val segmentsIndexesIncludeCurrent = flight.flightSegments.slice(0..i).toIntArray()

            val args = Bundle()
            args.putLong("departureTimeEpoch", flight.departureDateEpoch)
            args.putIntArray("segmentsIndexesIncludeCurrent", segmentsIndexesIncludeCurrent)

            val fragItem = SearchFlightInfoItem().apply { arguments = args }
            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(container.id, fragItem)
            }

            val departureDateEpoch = flight.departureDateEpoch
            val destinationDateEpoch = flight.destinationDateEpoch

            val totalFlightTimeStr = LocalDateConverter()
                .fromEpochSecondToTimeString(destinationDateEpoch - departureDateEpoch)

            totalFlightTimeTextView.text = "Общее время перелёта - $totalFlightTimeStr"
            totalPriceTextView.text = "Общая цена билета - ${flight.totalPrice}"
        }

        buyButton.setOnClickListener {
            val departure = flight.flightAirportCodes.first()
            val destination = flight.flightAirportCodes.last()
            val totalPrice = flight.totalPrice

            val titleText: String
            val messageText: String

            if (purchasedViewModel.purchasedLiveData.value?.any { it.flightIndex == flight.flightIndex } == true) {
                titleText = "Повторная покупка билета"
                messageText =
                    "Вы уверены, что хотите повторно купить билет из $departure в $destination за $totalPrice?"
            } else {
                titleText = "Покупка билета"
                messageText =
                    "Вы уверены, что хотите купить билет из $departure в $destination за $totalPrice?"
            }

            AlertDialog.Builder(requireContext())
                .setTitle(titleText)
                .setMessage(messageText)
                .setPositiveButton("Да") { _, _ ->
                    insert(flight)

                    val message = "Билет из $departure в $destination был куплен"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    dismiss()
                }
                .setNegativeButton("Нет") { _, _ -> }
                .show()
        }
    }

    private fun insert(clicked: SearchResultFlight) {
        val purchased = Purchased(flightIndex = clicked.flightIndex)
        purchasedViewModel.insert(purchased)

//        TODO get it back
//        purchasedViewModel.scheduleNotification(
//            requireContext(), clicked.destinationDateEpoch, purchased.flightIndex,
//        )
    }

    private fun Double.round() = (this * 100).roundToInt() / 100.toDouble()

}