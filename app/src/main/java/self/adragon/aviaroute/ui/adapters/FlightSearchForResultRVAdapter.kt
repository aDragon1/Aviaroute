package self.adragon.aviaroute.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import self.adragon.aviaroute.R

class FlightSearchForResultRVAdapter(private val onClick: (String, Int) -> Any) :
    RecyclerView.Adapter<FlightSearchForResultRVAdapter.FlightSearchForResultVH>() {

    var data: List<Pair<String, Int>> = emptyList()
    fun fillData(airportNames: List<Pair<String, Int>>) {
        data = airportNames
        notifyDataSetChanged()
    }

    inner class FlightSearchForResultVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchItemTextView: TextView = itemView.findViewById(R.id.searchItemTextView)
        val searchItemCardView: CardView = itemView.findViewById(R.id.searchItemCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightSearchForResultVH {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_item, parent, false)

        return FlightSearchForResultVH(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FlightSearchForResultVH, position: Int) {
        val item = data[position]
        holder.searchItemTextView.text = item.first

        holder.searchItemCardView.setOnClickListener {
            onClick(item.first, item.second)
        }
    }
}