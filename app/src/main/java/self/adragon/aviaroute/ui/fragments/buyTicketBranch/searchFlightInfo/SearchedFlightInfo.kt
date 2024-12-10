package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
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

class SearchedFlightInfo(private val flight: SearchResultFlight) :
    DialogFragment(R.layout.search_result_flight_info), OnClickListener {

    var isShown = false

    private lateinit var backImageButton: ImageButton
    private lateinit var flightSearchInfoCodesTextView: TextView

    private lateinit var buyButton: Button
    private lateinit var totalFlightTimeTextView: TextView
    private lateinit var totalPriceTextView: TextView

    private lateinit var container: LinearLayout

    private val purchasedViewModel: PurchasedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext(), R.style.FullScreenDialogTheme)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isShown = true

        initViews(view)

        val codes = "->"
        flightSearchInfoCodesTextView.text = codes
        buyButton.text = "Выбрать билет"

        flight.flightSegments.forEachIndexed { i, _ ->
            // All segments from first to current
            val segmentsIndexesIncludeCurrent = flight.flightSegments.slice(0..i).toIntArray()

            val args = Bundle()
            args.putLong("departureTimeEpoch", flight.departureDateEpoch)
            args.putIntArray("segmentsIndexesIncludeCurrent", segmentsIndexesIncludeCurrent)

            val fragItem = SearchedFlightInfoItem().apply { arguments = args }
            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(container.id, fragItem)
            }

            val departureDateEpoch = flight.departureDateEpoch
            val destinationDateEpoch = flight.destinationDateEpoch

            val totalFlightTimeStr = LocalDateConverter()
                .fromEpochSecondToTimeString(destinationDateEpoch - departureDateEpoch)

            totalFlightTimeTextView.text = "Общее время перелёта - $totalFlightTimeStr"
            totalPriceTextView.text = "Общая цена билета - ${flight.totalPrice.round()} у.е."
        }
    }

    private fun initViews(view: View) {
        container = view.findViewById(R.id.searchResultFlightInfoFragmentContainer)

        backImageButton = view.findViewById(R.id.backImageButton)
        flightSearchInfoCodesTextView = view.findViewById(R.id.flightSearchInfoCodesTextView)

        buyButton = view.findViewById(R.id.buyButton)
        totalFlightTimeTextView = view.findViewById(R.id.totalFlightTimeTextView)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)

        buyButton.setOnClickListener(this)
        backImageButton.setOnClickListener(this)
    }

    private fun insert(clicked: SearchResultFlight) {
        val purchased = Purchased(flightIndex = clicked.flightIndex)
        purchasedViewModel.insert(purchased)

//        TODO get it back
//        purchasedViewModel.scheduleNotification(
//            requireContext(), clicked.destinationDateEpoch, purchased.flightIndex,
//        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buyButton -> {
                val departure = flight.flightAirportCodes.first()
                val destination = flight.flightAirportCodes.last()
                val totalPrice = flight.totalPrice.round()

                val cond = purchasedViewModel.purchasedLiveData.value
                    ?.any { it.flightIndex == flight.flightIndex } == true

                val (titleText, messageText) = if (cond) "Повторное добавление билета" to
                        "Вы уверены, что хотите повторно выбрать билет из $departure в $destination за $totalPrice?"
                else "Добавление билета" to
                        "Вы уверены, что хотите выбрать билет из $departure в $destination за $totalPrice?"

                AlertDialog.Builder(requireContext())
                    .setTitle(titleText)
                    .setMessage(messageText)
                    .setPositiveButton("Да") { _, _ ->
                        insert(flight)
                        val message = "Билет из $departure в $destination был выбран"
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        dismiss()
                    }
                    .setNegativeButton("Нет") { _, _ -> }
                    .show()
            }

            R.id.backImageButton -> dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShown = false
    }

    private fun Double.round() = (this * 100).roundToInt() / 100.toDouble()
}