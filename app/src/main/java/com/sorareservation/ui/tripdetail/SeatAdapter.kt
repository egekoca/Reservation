package com.sorareservation.ui.tripdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sorareservation.R
import com.sorareservation.databinding.SeatItemBinding
import com.sorareservation.model.Seat
import com.sorareservation.model.SeatStatus

/**
 * Adapter for displaying seats in a grid layout
 */
class SeatAdapter(
    private val seats: List<Seat>,
    private val onSeatClick: (Seat) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {
    
    inner class SeatViewHolder(private val binding: SeatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(seat: Seat) {
            binding.seatNumberTextView.text = seat.number.toString()
            
            // Set background color based on seat status
            val backgroundColor = when (seat.status) {
                SeatStatus.AVAILABLE -> ContextCompat.getColor(binding.root.context, R.color.seat_available)
                SeatStatus.OCCUPIED -> ContextCompat.getColor(binding.root.context, R.color.seat_occupied)
                SeatStatus.SELECTED -> ContextCompat.getColor(binding.root.context, R.color.seat_selected)
            }
            
            binding.seatCard.setCardBackgroundColor(backgroundColor)
            
            // Set text color based on status
            val textColor = when (seat.status) {
                SeatStatus.AVAILABLE -> ContextCompat.getColor(binding.root.context, R.color.white)
                SeatStatus.OCCUPIED -> ContextCompat.getColor(binding.root.context, R.color.white)
                SeatStatus.SELECTED -> ContextCompat.getColor(binding.root.context, R.color.white)
            }
            
            binding.seatNumberTextView.setTextColor(textColor)
            
            // Disable click for occupied seats
            binding.root.isEnabled = seat.status != SeatStatus.OCCUPIED
            binding.root.alpha = if (seat.status == SeatStatus.OCCUPIED) 0.5f else 1.0f
            
            binding.root.setOnClickListener {
                if (seat.status != SeatStatus.OCCUPIED) {
                    onSeatClick(seat)
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = SeatItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SeatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(seats[position])
    }
    
    override fun getItemCount(): Int = seats.size
}

