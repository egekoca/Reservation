package com.sorareservation.ui.reservationlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sorareservation.databinding.ReservationItemBinding
import com.sorareservation.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying reservations in RecyclerView
 */
class ReservationAdapter(
    private val reservations: List<Reservation>,
    private val onShareClick: (Reservation) -> Unit,
    private val onCancelClick: (Reservation) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    inner class ReservationViewHolder(private val binding: ReservationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(reservation: Reservation) {
            val trip = reservation.trip
            
            if (trip != null) {
                binding.departureCityTextView.text = trip.departureCity
                binding.arrivalCityTextView.text = trip.arrivalCity
                binding.dateTextView.text = dateFormat.format(trip.departureDate)
                binding.timeTextView.text = trip.departureTime
            } else {
                binding.departureCityTextView.text = "N/A"
                binding.arrivalCityTextView.text = "N/A"
                binding.dateTextView.text = "N/A"
                binding.timeTextView.text = "N/A"
            }
            
            binding.seatsTextView.text = reservation.getSeatNumbersString()
            binding.totalPriceTextView.text = "${reservation.totalPrice} TL"
            
            binding.shareButton.setOnClickListener {
                onShareClick(reservation)
            }
            
            binding.cancelButton.setOnClickListener {
                onCancelClick(reservation)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val binding = ReservationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReservationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.bind(reservations[position])
    }
    
    override fun getItemCount(): Int = reservations.size
}

