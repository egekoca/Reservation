package com.sorareservation.ui.confirmation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sorareservation.R
import com.sorareservation.databinding.SelectedSeatItemBinding
import com.sorareservation.model.Gender
import com.sorareservation.model.Seat

/**
 * Adapter for displaying selected seats in confirmation screen
 */
class SelectedSeatAdapter(
    private val seats: List<Seat>
) : RecyclerView.Adapter<SelectedSeatAdapter.SeatViewHolder>() {
    
    inner class SeatViewHolder(private val binding: SelectedSeatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(seat: Seat) {
            binding.seatNumberTextView.text = seat.number.toString()
            
            // Set background color based on gender
            val backgroundColor = when (seat.gender) {
                Gender.MALE -> ContextCompat.getColor(binding.root.context, R.color.seat_male)
                Gender.FEMALE -> ContextCompat.getColor(binding.root.context, R.color.seat_female)
                null -> ContextCompat.getColor(binding.root.context, R.color.seat_selected)
            }
            
            binding.root.setCardBackgroundColor(backgroundColor)
            binding.seatNumberTextView.setTextColor(
                ContextCompat.getColor(binding.root.context, R.color.white)
            )
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = SelectedSeatItemBinding.inflate(
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

