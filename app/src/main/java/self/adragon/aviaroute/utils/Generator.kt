package self.adragon.aviaroute.utils

import android.util.Log
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import self.adragon.aviaroute.data.model.Airport
import self.adragon.aviaroute.data.model.Flight
import self.adragon.aviaroute.data.model.Segment
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.min
import kotlin.math.roundToInt

@Suppress("SpellCheckingInspection")
class Generator {
    private val airportCodes = listOf("JFK", "LAX", "SFO", "ORD", "HND")
    private val airportsInfoMap = mapOf(
        "JFK" to listOf("John F Kennedy International Airport", "New York, US: +1 (718) 244 4444"),
        "LAX" to listOf("Los Angeles International Airport", "Los Angeles, US: +1 (310) 646 5252 "),
        "SFO" to listOf(
            "San Francisco International Airport", "San Francisco, US: +1 (650) 821 8211"
        ),
        "ORD" to listOf("Chicago O'Hare International Airport", "Chicago, US: +1 (800) 832 6352"),
        "HND" to listOf("Tokyo Haneda International Airport", "Tokyo, Japan: +81 (0) 3 5757 8111"),
    )

    private val minPrice = 1f
    private val maxPrice = 100f

    private val zoneID = ZoneId.systemDefault()
    private val minDate = LocalDate.now().atStartOfDay(zoneID).toEpochSecond()
    private val maxDate = LocalDate.of(2025, 1, 1).atStartOfDay(zoneID).toEpochSecond()

    // Seconds in _
    private val SECOND = 1
    private val MINUTE = 60 * SECOND
    private val HOUR = 60 * MINUTE
    private val DAY = 24 * HOUR

    private val minFlightTime = HOUR
    private val maxFlightTime = DAY

    private fun generateAirport(index: Int): Airport {
        val code = airportCodes[index - 1]
        val (name, info) = airportsInfoMap[code]!!

        return Airport(index, code, name, info)
    }

    private fun generateSegment(index: Int, departureIndex: Int, destinationIndex: Int): Segment {
        val price = (minPrice + Math.random() * (maxPrice - minPrice)).round()

        val flightTime = (minFlightTime..maxFlightTime).random().toLong()
        return Segment(index, departureIndex, destinationIndex, flightTime, price, "")
    }

    private fun takeSequentialSegments(segments: List<Segment>, n: Int): List<Int> {
        val resultSegments = mutableListOf<Int>()

        val startSegment = segments.random()
        resultSegments.add(startSegment.segmentIndex)

        var currentDestination = startSegment.destinationIndex
        for (i in 2..n) {
            val nextSegment = segments.firstOrNull { it.departureIndex == currentDestination }
                ?: return resultSegments

            resultSegments.add(nextSegment.segmentIndex)
            currentDestination = nextSegment.destinationIndex
        }

        return resultSegments
    }

    fun generateAirports(numAirports: Int) =
        (1..min(airportCodes.size, numAirports)).map { generateAirport(it) }

    fun generateSegments(numSegments: Int) = (1..numSegments).map { index ->
        val departureIndex = airportCodes.mapIndexed { i, s -> s to i }.random().second
        val destinationIndex = airportCodes.mapIndexed { i, s -> s to i }
            .filter { it.second != departureIndex }.random().second

        generateSegment(index, departureIndex + 1, destinationIndex + 1)
    }

    // change to flow + somehow split that mess into pices
    fun generateFlights(
        segments: List<Segment>, numFlights: Int, maxSegmentsPerFlight: Int
    ) = (1..numFlights).map { flightIndex ->
        Log.d("mytag", "process flight #$flightIndex")

        val segmentsSize = (1..maxSegmentsPerFlight).random()
        val flightSegments = takeSequentialSegments(segments, segmentsSize)

        val departureDateEpochSeconds = (minDate..maxDate).random()

        flightSegments.mapIndexed { segPosition, segIndex ->
            Flight(flightIndex, segIndex, segPosition + 1, departureDateEpochSeconds)
        }
    }.flatten()

//    fun generateFlights(segments: List<Segment>, numFlights: Int, maxSegmentsPerFlight: Int) =
//        channelFlow {
//            repeat(numFlights - 1) { flightIndex ->
//                launch {
//                    val segmentsSize = (1..maxSegmentsPerFlight).random()
//                    val flightSegments = takeSequentialSegments(segments, segmentsSize)
//
//                    val departureEpochSec = (minDate..maxDate).random()
//
//                    flightSegments.mapIndexed { segPosition, segIndex ->
//                        val f =
//                            Flight(flightIndex + 1, segIndex, segPosition + 1, departureEpochSec)
//                        send(f)
//                    }
//                }
//            }
//        }

    private fun Double.round() = (this * 100).roundToInt() / 100.toDouble()
}