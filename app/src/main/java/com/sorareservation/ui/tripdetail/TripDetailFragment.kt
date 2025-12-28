package com.sorareservation.ui.tripdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentTripDetailBinding
import com.sorareservation.model.Seat
import com.sorareservation.model.SeatStatus
import com.sorareservation.model.Trip
import com.sorareservation.ui.reservationlist.ReservationListActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for displaying trip details and seat selection
 */
class TripDetailFragment : Fragment() {
    
    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    
    private var trip: Trip? = null
    private var adapter: SeatAdapter? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    companion object {
        private const val ARG_TRIP_ID = "trip_id"
        
        /**
         * Create new instance of TripDetailFragment
         */
        fun newInstance(tripId: java.util.UUID?): TripDetailFragment {
            val fragment = TripDetailFragment()
            tripId?.let {
                fragment.arguments = Bundle().apply {
                    putSerializable(ARG_TRIP_ID, it)
                }
            }
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val tripId = arguments?.getSerializable(ARG_TRIP_ID) as? UUID
        trip = tripId?.let { SeferLab.getTrip(it) }
        
        if (trip == null) {
            Toast.makeText(requireContext(), "Trip not found", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }
        
        // Restore selected seats from saved state
        savedInstanceState?.let {
            val selectedSeats = it.getIntegerArrayList("selected_seats")
            selectedSeats?.forEach { seatNumber ->
                trip?.selectSeats(listOf(seatNumber))
            }
        }
        
        setupTripInfo()
        setupSeatGrid()
        setupBookButton()
        updateSelectedSeatsSummary()
    }
    
    private fun setupTripInfo() {
        trip?.let {
            binding.departureCityTextView.text = it.departureCity
            binding.arrivalCityTextView.text = it.arrivalCity
            binding.dateTextView.text = dateFormat.format(it.departureDate)
            binding.timeTextView.text = it.departureTime
            binding.priceTextView.text = "${it.price} TL"
        }
    }
    
    private fun setupSeatGrid() {
        trip?.let { currentTrip ->
            adapter = SeatAdapter(currentTrip.seats) { seat ->
                handleSeatClick(seat)
            }
            
            // Use GridLayoutManager with 5 columns (typical bus layout)
            binding.seatRecyclerView.layoutManager = GridLayoutManager(requireContext(), 5)
            binding.seatRecyclerView.adapter = adapter
        }
    }
    
    private fun handleSeatClick(seat: Seat) {
        trip?.let { currentTrip ->
            when (seat.status) {
                SeatStatus.AVAILABLE -> {
                    // Select seat
                    currentTrip.selectSeats(listOf(seat.number))
                    adapter?.notifyItemChanged(currentTrip.seats.indexOf(seat))
                    updateSelectedSeatsSummary()
                }
                SeatStatus.SELECTED -> {
                    // Deselect seat
                    currentTrip.deselectSeats(listOf(seat.number))
                    adapter?.notifyItemChanged(currentTrip.seats.indexOf(seat))
                    updateSelectedSeatsSummary()
                }
                SeatStatus.OCCUPIED -> {
                    // Do nothing, seat is already booked
                }
            }
        }
    }
    
    private fun updateSelectedSeatsSummary() {
        trip?.let { currentTrip ->
            val selectedSeats = currentTrip.getSelectedSeats()
            
            if (selectedSeats.isEmpty()) {
                binding.selectedSeatsTextView.text = "No seats selected"
                binding.totalPriceTextView.text = "0 TL"
            } else {
                val seatNumbers = selectedSeats.map { it.number }.sorted()
                binding.selectedSeatsTextView.text = seatNumbers.joinToString(", ")
                val totalPrice = currentTrip.price * selectedSeats.size
                binding.totalPriceTextView.text = "$totalPrice TL"
            }
        }
    }
    
    private fun setupBookButton() {
        binding.bookButton.setOnClickListener {
            attemptBooking()
        }
    }
    
    private fun attemptBooking() {
        trip?.let { currentTrip ->
            val selectedSeats = currentTrip.getSelectedSeats()
            
            if (selectedSeats.isEmpty()) {
                Toast.makeText(requireContext(), R.string.no_seats_selected, Toast.LENGTH_SHORT).show()
                return
            }
            
            val seatNumbers = selectedSeats.map { it.number }
            val reservation = SeferLab.createReservation(currentTrip.id, seatNumbers)
            
            if (reservation != null) {
                Toast.makeText(requireContext(), R.string.booking_success, Toast.LENGTH_SHORT).show()
                
                // Clear selected seats
                currentTrip.clearSelectedSeats()
                adapter?.notifyDataSetChanged()
                updateSelectedSeatsSummary()
                
                // Navigate to reservations list
                val intent = ReservationListActivity.newIntent(requireContext())
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Booking failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        trip?.let { currentTrip ->
            val selectedSeats = currentTrip.getSelectedSeats()
            val seatNumbers = selectedSeats.map { it.number }
            outState.putIntegerArrayList("selected_seats", ArrayList(seatNumbers))
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

