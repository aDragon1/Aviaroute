package self.adragon.aviaroute.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R
import self.adragon.aviaroute.data.model.searchResult.SearchResultFlight

class PurchasedRVAdapter(private val onClick: (SearchResultFlight) -> Any) :
    RecyclerView.Adapter<PurchasedRVAdapter.FlightRVViewHolder>() {

    private var flights: List<SearchResultFlight> = listOf()

    fun fillData(newFlights: List<SearchResultFlight>) {
        flights = newFlights
    }


    inner class FlightRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val departureTextView: TextView = itemView.findViewById(R.id.departureTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.flightSummaryTextView)
        val departureDateTextView: TextView = itemView.findViewById(R.id.departureDateTextView)
        val destinationDateTextView: TextView = itemView.findViewById(R.id.destinationDateTextView)

        val transferSizeTextView: TextView = itemView.findViewById(R.id.transferSizeTextView)
        val extraArrowRightImageView: ImageView =
            itemView.findViewById(R.id.extraArrowRightImageView)

        val flightCardView: CardView = itemView.findViewById(R.id.flightCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightRVViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.purchased_list_item, parent, false)

        return FlightRVViewHolder(itemView)
    }

    override fun getItemCount(): Int = flights.size


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FlightRVViewHolder, position: Int) {

        val flight = flights[position]
        val depDate = flight.departureDateString.split(", ")
        val destDate = flight.destinationDateString.split(", ")
        holder.apply {
            departureTextView.text = flight.flightAirportCodes.first()
            destinationTextView.text = flight.flightAirportCodes.last()
            priceTextView.text = "${"%.2f".format(flight.totalPrice)} у.е."
            departureDateTextView.text = "${depDate[0]},\n ${depDate[1]}"
            destinationDateTextView.text = "${destDate[0]},\n ${destDate[1]}"

            transferSizeTextView.visibility = View.GONE
            extraArrowRightImageView.visibility = View.GONE

            val codes = flight.flightAirportCodes
            if (codes.size > 2) {
                transferSizeTextView.visibility = View.VISIBLE
                extraArrowRightImageView.visibility = View.VISIBLE

                transferSizeTextView.text =
                    "[${if (codes.size == 3) codes[1] else (codes.size - 1).toString()}]"
            }
            flightCardView.setOnClickListener { onClick(flight) }
        }
    }
}