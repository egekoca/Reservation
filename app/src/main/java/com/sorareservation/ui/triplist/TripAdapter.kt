package com.sorareservation.ui.triplist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sorareservation.databinding.TripItemBinding
import com.sorareservation.model.Trip
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying trips in RecyclerView
 */
class TripAdapter(
    private var trips: List<Trip>,
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class TripViewHolder(private val binding: TripItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            binding.departureCityTextView.text = trip.departureCity
            binding.arrivalCityTextView.text = trip.arrivalCity
            binding.dateTextView.text = dateFormat.format(trip.departureDate)
            binding.timeTextView.text = trip.departureTime
            binding.priceTextView.text = "${trip.price} TL"
            binding.availableSeatsTextView.text = trip.getAvailableSeatsCount().toString()

            binding.root.setOnClickListener {
                onTripClick(trip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = TripItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size

    /**
     * Update trip list and notify adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTrips: List<Trip>) {
        this.trips = newTrips
        notifyDataSetChanged()
    }
}
