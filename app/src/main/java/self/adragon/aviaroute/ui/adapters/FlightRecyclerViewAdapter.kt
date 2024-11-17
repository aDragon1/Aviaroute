package self.adragon.aviaroute.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

class FlightRecyclerViewAdapter(
    private val onClick: (SearchResultFlight) -> Any
) : RecyclerView.Adapter<FlightRecyclerViewAdapter.FlightRVViewHolder>() {

    private var flights: List<SearchResultFlight> = emptyList()

    fun setData(newFlights: List<SearchResultFlight>) {
        flights = newFlights
        notifyDataSetChanged()
    }

    inner class FlightRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.idTextView)
        val airlineTextView: TextView = itemView.findViewById(R.id.airlineTextView)
        val departureTextView: TextView = itemView.findViewById(R.id.departureTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val destinationDateTextView: TextView = itemView.findViewById(R.id.destinationDateTextView)

        val transferSizeTextView: TextView = itemView.findViewById(R.id.transferSizeTextView)
        val extraArrowRightImageView: ImageView =
            itemView.findViewById(R.id.extraArrowRightImageView)

        val flightCardView: CardView = itemView.findViewById(R.id.flightCardView)

        init {
            TODO("Not implemented yet (так впадлу)")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightRVViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.flight_list_item, parent, false)

        return FlightRVViewHolder(itemView)
    }

    override fun getItemCount(): Int = flights.size


    //    TODO don't forget to add airline later
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FlightRVViewHolder, position: Int) {

        val flight = flights[position]
        val segments = flight.flightSegments
        val segSize = segments.size
        holder.apply {
//            idTextView.text = flight.flightIndex.toString()
//            airlineTextView.text = "Airline add late"
//            departureTextView.text = flight.departureAirport.code
//            destinationTextView.text = flight.destinationAirport.code
//            priceTextView.text = "${flight.totalPrice} у.е."
//            departureDateTextView.text = flight.departureDateString
//            destinationDateTextView.text = flight.destinationDateString
//
//            transferSizeTextView.visibility = View.GONE
//            extraArrowRightImageView.visibility = View.GONE
//
//            if (segSize > 1) {
//                transferSizeTextView.visibility = View.VISIBLE
//                extraArrowRightImageView.visibility = View.VISIBLE
//
//                val firstDestinationCode = segments.first().destinationAirport.code
//                transferSizeTextView.text =
//                    "[${if (segSize == 2) firstDestinationCode else (segments.size - 1).toString()}]"
//            }

            flightCardView.setOnClickListener { onClick(flight) }
        }
    }
}