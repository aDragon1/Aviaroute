package self.adragon.aviaroute.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

class FlightRecyclerViewAdapter(private val onClick: (SearchResultFlight) -> Any) :
    RecyclerView.Adapter<FlightRecyclerViewAdapter.FlightRVViewHolder>() {

    private var flights: List<SearchResultFlight> = listOf()

    fun fillData(newFlights: List<SearchResultFlight>) {
        flights = newFlights
    }

    inner class FlightRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countTransferTextView: TextView = itemView.findViewById(R.id.countTransfer)
        val priceTextView: TextView = itemView.findViewById(R.id.flightSummaryTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val destinationDateTextView: TextView = itemView.findViewById(R.id.destinationDateTextView)

        val flightCardView: CardView = itemView.findViewById(R.id.flightCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightRVViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_flight_list_item, parent, false)

        return FlightRVViewHolder(itemView)
    }

    override fun getItemCount(): Int = flights.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FlightRVViewHolder, position: Int) {

        val flight = flights[position]
        val segmentsSize = flight.flightSegments.size
        holder.apply {
            priceTextView.text = "${"%.2f".format(flight.totalPrice)} у.е."
            departureDateTextView.text = flight.departureDateString
            destinationDateTextView.text = flight.destinationDateString

            flightCardView.setOnClickListener { onClick(flight) }

            val message = when (segmentsSize) {
                1 -> "Прямой"
                2 -> "Транзит"
                else -> "Количество пересадок - $segmentsSize"
            }
            countTransferTextView.text = message
        }
    }
}