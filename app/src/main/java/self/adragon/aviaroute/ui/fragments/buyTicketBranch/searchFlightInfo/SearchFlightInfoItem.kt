package self.adragon.aviaroute.ui.fragments.buyTicketBranch.searchFlightInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.database.FlightsDatabase
import self.adragon.aviaroute.data.model.searchResult.SearchResultSegment
import self.adragon.aviaroute.data.model.typeConverters.LocalDateConverter
import self.adragon.aviaroute.data.repo.AirportRepository
import self.adragon.aviaroute.data.repo.SegmentRepository

class SearchFlightInfoItem : Fragment(R.layout.search_result_flight_info_item) {

    private lateinit var flightNumberTextView: TextView
    private lateinit var departureTimeTextView: TextView
    private lateinit var departureAirportTextView: TextView
    private lateinit var destinationTimeTextView: TextView
    private lateinit var destinationAirportTextView: TextView
    private lateinit var flightSummaryTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val departureTimeEpoch = arguments?.getLong("departureTimeEpoch", -1L) ?: -1L
        val segmentsIndexesIncludeCurrent = arguments
            ?.getIntArray("segmentsIndexesIncludeCurrent") ?: IntArray(0)

        setViews(view)
        if (departureTimeEpoch == -1L) {
            flightSummaryTextView.text = "Ошибка"
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = FlightsDatabase.getDatabase(requireContext())
            val segmentRepository = SegmentRepository(db.segmentsDAO())
            val airportRepository = AirportRepository(db.airportsDAO())

            val searchResultSegment = fetchSearchResultSegment(
                segmentRepository, airportRepository,
                segmentsIndexesIncludeCurrent, departureTimeEpoch
            )

            withContext(Dispatchers.Main) {
                displaySearchResultSegment(searchResultSegment)
            }
        }
    }

    private fun setViews(itemView: View) {
        flightNumberTextView = itemView.findViewById(R.id.flightNumberTextView)
        departureTimeTextView = itemView.findViewById(R.id.departureTimeTextView)
        departureAirportTextView = itemView.findViewById(R.id.departureAirportTextView)
        destinationTimeTextView = itemView.findViewById(R.id.destinationTimeTextView)
        destinationAirportTextView = itemView.findViewById(R.id.destinationAirportTextView)
        flightSummaryTextView = itemView.findViewById(R.id.flightSummaryTextView)
    }

    private fun fetchSearchResultSegment(
        segmentRepository: SegmentRepository, airportRepository: AirportRepository,
        segmentIndexes: IntArray, departureTimeEpoch: Long
    ): SearchResultSegment {
        val segmentDepartureTime =
            segmentRepository.currentSegmentDepartureTime(segmentIndexes, departureTimeEpoch)

        val currentSegment = segmentRepository.getByIndex(segmentIndexes.last())
        val curFlightTime = currentSegment.flightTimeEpochSeconds

        val indexes = listOf(currentSegment.departureIndex, currentSegment.destinationIndex)
        val (depAirport, destAirport) = airportRepository.getAirportByIndexes(indexes)

        val depAirportString = "${depAirport?.name}, ${depAirport?.code}"
        val destAirportString = "${destAirport?.name}, ${destAirport?.code}"

        val destinationTimeEpochSeconds = departureTimeEpoch + curFlightTime

        val converter = LocalDateConverter()
        val departureDateString =
            converter.fromEpochSecondsStringDateTime(segmentDepartureTime)
        val destDateString = converter.fromEpochSecondsStringDateTime(destinationTimeEpochSeconds)
        val flightTimeString = converter.fromEpochSecondToTimeString(curFlightTime)

        return SearchResultSegment(
            currentSegment.flightNumber,
            departureDateString,
            destDateString,
            depAirportString,
            destAirportString,
            currentSegment.price,
            flightTimeString
        )
    }

    @SuppressLint("SetTextI18n")
    private fun displaySearchResultSegment(details: SearchResultSegment) {
        val flightNumber = "Номер рейса - ${details.flightNumber}"
        val flightPrice = "Цена: ${details.price} у.е."
        val flightTIme = "Время в пути: ${details.flightTime}"

        flightNumberTextView.text = flightNumber

        departureTimeTextView.text = details.departureDate
        destinationTimeTextView.text = details.destinationDate

        departureAirportTextView.text = details.departureAirport
        destinationAirportTextView.text = details.destinationAirport

        flightSummaryTextView.text = "$flightPrice\n$flightTIme"
    }
}
