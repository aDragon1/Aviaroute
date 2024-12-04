package self.adragon.aviaroute.data.model.searchResult

data class SearchResultSegment(
    val flightNumber: String,
    val departureDate: String,
    val destinationDate: String,
    val departureAirport: String,
    val destinationAirport: String,
    val price: Double,
    val flightTime: String
)