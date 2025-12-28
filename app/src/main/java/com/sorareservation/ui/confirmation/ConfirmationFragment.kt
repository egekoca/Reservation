package com.sorareservation.ui.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentConfirmationBinding
import com.sorareservation.model.Seat
import com.sorareservation.model.Trip
import com.sorareservation.ui.reservationlist.ReservationListActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

/**
 * Fragment for confirming reservation and payment
 */
class ConfirmationFragment : Fragment() {
    
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!
    
    private var tripId: UUID? = null
    private var seatNumbers: List<Int> = emptyList()
    private var selectedSeats: List<Seat> = emptyList()
    private var adapter: SelectedSeatAdapter? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    companion object {
        private const val ARG_TRIP_ID = "trip_id"
        private const val ARG_SEAT_NUMBERS = "seat_numbers"
        
        /**
         * Create new instance of ConfirmationFragment
         */
        fun newInstance(tripId: UUID?, seatNumbers: List<Int>): ConfirmationFragment {
            val fragment = ConfirmationFragment()
            val args = Bundle().apply {
                tripId?.let { putSerializable(ARG_TRIP_ID, it) }
                putIntegerArrayList(ARG_SEAT_NUMBERS, ArrayList(seatNumbers))
            }
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tripId = arguments?.getSerializable(ARG_TRIP_ID) as? UUID
        seatNumbers = arguments?.getIntegerArrayList(ARG_SEAT_NUMBERS) ?: arrayListOf()
        
        val trip = tripId?.let { SeferLab.getTrip(it) }
        
        val context = context ?: return
        val activity = activity ?: return
        
        if (trip == null || seatNumbers.isEmpty()) {
            Toast.makeText(context, R.string.invalid_reservation_data, Toast.LENGTH_SHORT).show()
            activity.finish()
            return
        }
        
        // Get selected seats with their genders
        selectedSeats = seatNumbers.mapNotNull { seatNum ->
            trip.seats.find { it.number == seatNum && it.isSelected() }
        }
        
        if (selectedSeats.isEmpty()) {
            Toast.makeText(context, R.string.no_seats_selected_error, Toast.LENGTH_SHORT).show()
            activity.finish()
            return
        }
        
        setupToolbar()
        setupTripInfo(trip)
        setupSelectedSeats()
        setupConfirmButton(trip)
    }
    
    private fun setupToolbar() {
        val context = context ?: return
        val activity = activity ?: return
        if (activity !is androidx.appcompat.app.AppCompatActivity) return
        
        try {
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.title = getString(R.string.confirm_and_continue)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupTripInfo(trip: Trip) {
        binding.departureCityTextView.text = trip.departureCity
        binding.arrivalCityTextView.text = trip.arrivalCity
        binding.dateTextView.text = dateFormat.format(trip.departureDate)
        binding.timeTextView.text = trip.departureTime
        
        val totalPrice = trip.price * selectedSeats.size
        binding.totalPriceTextView.text = "$totalPrice TL"
        
        // Price breakdown
        val pricePerSeat = trip.price
        val seatCount = selectedSeats.size
        if (seatCount > 1) {
            binding.priceBreakdownTextView.text = "$seatCount seats × $pricePerSeat TL = $totalPrice TL"
        } else {
            binding.priceBreakdownTextView.text = "1 seat × $pricePerSeat TL = $totalPrice TL"
        }
    }
    
    private fun setupSelectedSeats() {
        val context = context ?: return
        adapter = SelectedSeatAdapter(selectedSeats)
        binding.selectedSeatsRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.selectedSeatsRecyclerView.adapter = adapter
    }
    
    private fun setupConfirmButton(trip: Trip) {
        binding.confirmButton.setOnClickListener {
            confirmReservation(trip)
        }
    }
    
    private fun confirmReservation(trip: Trip) {
        val context = context ?: return
        val activity = activity ?: return
        if (!isAdded) return
        
        val reservation = SeferLab.createReservation(trip.id, seatNumbers)
        
        if (reservation != null) {
            Toast.makeText(context, R.string.booking_success, Toast.LENGTH_SHORT).show()
            
            // Navigate to reservations list
            val intent = ReservationListActivity.newIntent(context)
            startActivity(intent)
            activity.finish()
        } else {
            Toast.makeText(context, R.string.booking_failed, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

