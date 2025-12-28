package com.sorareservation.ui.tripdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sorareservation.R
import com.sorareservation.databinding.SeatItemBinding
import com.sorareservation.model.Gender
import com.sorareservation.model.Seat
import com.sorareservation.model.SeatStatus

/**
 * Adapter for displaying seats in a grid layout
 */
class SeatAdapter(
    val seats: List<Seat>,
    private val onSeatClick: (Seat) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {
    
    inner class SeatViewHolder(private val binding: SeatItemBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(seat: Seat) {
            binding.seatNumberTextView.text = seat.number.toString()
            
            // Set background color based on seat status and gender
            val backgroundColor = when (seat.status) {
                SeatStatus.AVAILABLE -> ContextCompat.getColor(binding.root.context, R.color.seat_available)
                SeatStatus.OCCUPIED -> {
                    // Show gender color for occupied seats
                    when (seat.gender) {
                        Gender.MALE -> ContextCompat.getColor(binding.root.context, R.color.seat_male)
                        Gender.FEMALE -> ContextCompat.getColor(binding.root.context, R.color.seat_female)
                        null -> ContextCompat.getColor(binding.root.context, R.color.seat_occupied)
                    }
                }
                SeatStatus.SELECTED -> {
                    // Show gender color for selected seats
                    when (seat.gender) {
                        Gender.MALE -> ContextCompat.getColor(binding.root.context, R.color.seat_male)
                        Gender.FEMALE -> ContextCompat.getColor(binding.root.context, R.color.seat_female)
                        null -> ContextCompat.getColor(binding.root.context, R.color.seat_selected)
                    }
                }
            }
            
            binding.seatCard.setCardBackgroundColor(backgroundColor)
            
            // Set text color based on status
            val textColor = when (seat.status) {
                SeatStatus.AVAILABLE -> ContextCompat.getColor(binding.root.context, R.color.white)
                SeatStatus.OCCUPIED -> ContextCompat.getColor(binding.root.context, R.color.white)
                SeatStatus.SELECTED -> ContextCompat.getColor(binding.root.context, R.color.white)
            }
            
            binding.seatNumberTextView.setTextColor(textColor)
            
            // Show gender icon for occupied/selected seats
            if (seat.isOccupied() || seat.isSelected()) {
                binding.genderIconImageView.visibility = View.VISIBLE
                val genderIcon = when (seat.gender) {
                    Gender.MALE -> R.drawable.ic_male
                    Gender.FEMALE -> R.drawable.ic_female
                    else -> null
                }
                if (genderIcon != null) {
                    binding.genderIconImageView.setImageResource(genderIcon)
                    binding.genderIconImageView.setColorFilter(textColor)
                } else {
                    binding.genderIconImageView.visibility = View.GONE
                }
            } else {
                binding.genderIconImageView.visibility = View.GONE
            }
            
            // Disable click for occupied seats
            binding.root.isEnabled = seat.status != SeatStatus.OCCUPIED
            binding.root.alpha = if (seat.status == SeatStatus.OCCUPIED) 0.6f else 1.0f
            
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

