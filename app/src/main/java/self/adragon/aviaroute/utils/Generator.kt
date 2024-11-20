package self.adragon.aviaroute.utils

import self.adragon.aviaroute.data.model.Airport
import self.adragon.aviaroute.data.model.Flight
import self.adragon.aviaroute.data.model.Segment
import java.time.LocalDate
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

    private val minDate = LocalDate.now()
    private val maxDate = LocalDate.of(2026, 1, 1)

    // Seconds in _
    private val SECOND = 1
    private val MINUTE = 60 * SECOND
    private val HOUR = 60 * MINUTE
    private val DAY = 24 * HOUR

    private val minFlightTime = HOUR
    private val maxFlightTime = DAY

    private fun generateAirports(index: Int): Airport {
        val code = airportCodes[index - 1]
        val (name, info) = airportsInfoMap[code]!!

        return Airport(index, code, name, info)
    }

    private fun generateSegments(index: Int, departureIndex: Int, destinationIndex: Int): Segment {
        val price = (minPrice + Math.random() * (maxPrice - minPrice)).round()

        val flightTime = (minFlightTime..maxFlightTime).random().toLong()
        return Segment(
            index, departureIndex, destinationIndex, flightTime, price, "Im lazy to generate it"
        )
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

    fun generateDatabaseData(
        numAirports: Int, numSegments: Int, numFlights: Int, maxSegmentsPerFlight: Int
    ): Triple<List<Airport>, List<Segment>, List<Flight>> {

        val airports = (1..min(airportCodes.size, numAirports)).map { generateAirports(it) }
        val segments = (1..numSegments).map { index ->
            val departure = airports.random().airportIndex
            val destination = airports.filter { it.airportIndex != departure }.random().airportIndex

            generateSegments(index, departure, destination)
        }
        val flights = (1..numFlights).map { flightIndex ->
            val segmentsSize = (1..maxSegmentsPerFlight).random()
            val flightSegments = takeSequentialSegments(segments, segmentsSize)

            val departureDate = generateRandomDate()

            val epochSeconds = departureDate.toEpochDay() * DAY
            flightSegments.mapIndexed { segPosition, segIndex ->
                Flight(flightIndex, segIndex, segPosition + 1, epochSeconds)
            }
        }


        val resultFlights = mutableListOf<Flight>()
        flights.forEach { resultFlights.addAll(it) }

        return Triple(airports, segments, resultFlights)
    }

    private fun generateRandomDate(): LocalDate {
        val minDay = minDate.toEpochDay()
        val maxDay = maxDate.toEpochDay()

        return LocalDate.ofEpochDay((minDay..maxDay).random())
    }

    private fun Double.round() = (this * 100).roundToInt() / 100.toDouble()
}