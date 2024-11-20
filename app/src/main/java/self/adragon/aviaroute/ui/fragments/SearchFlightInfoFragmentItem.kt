package self.adragon.aviaroute.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.data.repo.SegmentRepository

class SearchFlightInfoFragmentItem : Fragment(R.layout.search_result_flight_info_item) {

    private lateinit var flightNumberTextView: TextView
    private lateinit var departureTimeTextView: TextView
    private lateinit var departureAirportTextView: TextView
    private lateinit var destinationTimeTextView: TextView
    private lateinit var destinationAirportTextView: TextView
    private lateinit var priceTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val departureTimeEpoch = arguments?.getLong("departureTimeEpoch", -1) ?: -1
        val segmentsIndexesIncludeCurrent =
            arguments?.getIntArray("segmentsIndexesIncludeCurrent") ?: IntArray(0)

        setViews(view)
        setStuff(departureTimeEpoch, segmentsIndexesIncludeCurrent)
    }

    private fun setViews(view: View) {
        flightNumberTextView = view.findViewById(R.id.flightNumberTextView)
        departureTimeTextView = view.findViewById(R.id.departureTimeTextView)
        departureAirportTextView = view.findViewById(R.id.departureAirportTextView)
        destinationTimeTextView = view.findViewById(R.id.destinationTimeTextView)
        destinationAirportTextView = view.findViewById(R.id.destinationAirportTextView)
        priceTextView = view.findViewById(R.id.priceTextView)
    }

    @SuppressLint("SetTextI18n")
    private fun setStuff(departureTimeEpoch: Long, segmentsIndexesIncludeCurrent: IntArray) =
        CoroutineScope(Dispatchers.IO).launch {
            val db = FlightsDatabase.getDatabase(requireContext())
            val segmentRepository = SegmentRepository(db.segmentsDAO())
            val airportRepository = AirportRepository(db.airportsDAO())

            val currentSegment = segmentRepository.getByIndex(segmentsIndexesIncludeCurrent.last())

            val otherSegmentsFlightTime =
                segmentsIndexesIncludeCurrent.map { segmentRepository.getByIndex(it) }
                    .sumOf { it.flightTimeEpochSeconds } - currentSegment.flightTimeEpochSeconds

            val departureAirport =
                airportRepository.getAirportByIndex(currentSegment.departureIndex)
            val destinationAirport =
                airportRepository.getAirportByIndex(currentSegment.destinationIndex)

            val depAirportString = "${departureAirport?.name}, ${departureAirport?.code}"
            val destAirportString = "${destinationAirport?.name}, ${destinationAirport?.code}"

            val fixedDepartureTimeEpochSeconds = departureTimeEpoch + otherSegmentsFlightTime
            val destinationTimeEpochSeconds =
                departureTimeEpoch + currentSegment.flightTimeEpochSeconds

            val converter = LocalDateConverter()
            val departureDateString =
                converter.fromEpochDayToStringDate(fixedDepartureTimeEpochSeconds)
            val destDateString = converter.fromEpochDayToStringDate(destinationTimeEpochSeconds)

            withContext(Dispatchers.Main) {
                Log.d("mytag", "\n~~~~~~")
                Log.d("mytag", "departureTimeEpoch - $departureTimeEpoch")
                Log.d("mytag", "otherSegmentsFlightTime - $otherSegmentsFlightTime")

                flightNumberTextView.text = "Номер рейса - ${currentSegment.flightNumber}"

                departureTimeTextView.text = departureDateString
                destinationTimeTextView.text = destDateString

                departureAirportTextView.text = depAirportString
                destinationAirportTextView.text = destAirportString

                priceTextView.text = "${currentSegment.price} у.е."
                Log.d("mytag", "seg - $currentSegment")
            }
        }
}