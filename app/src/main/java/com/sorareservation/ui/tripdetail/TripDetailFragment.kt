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
import com.sorareservation.databinding.DialogGenderWarningBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.app.AlertDialog
import com.sorareservation.model.Gender
import com.sorareservation.model.Seat
import com.sorareservation.model.SeatStatus
import com.sorareservation.model.Trip
import com.sorareservation.ui.confirmation.ConfirmationActivity
import com.sorareservation.ui.reservationlist.ReservationListActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

/**
 * Fragment for displaying trip details and seat selection
 */
class TripDetailFragment : Fragment() {
    
    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    
    private var trip: Trip? = null
    private var adapter: SeatAdapter? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var selectedGender: Gender? = null  // Currently selected gender for seat selection
    
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
        setupGenderSelection()
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
    
    private fun setupGenderSelection() {
        binding.maleButton.setOnClickListener {
            selectedGender = Gender.MALE
            updateGenderButtonStates()
        }
        
        binding.femaleButton.setOnClickListener {
            selectedGender = Gender.FEMALE
            updateGenderButtonStates()
        }
        
        // Default to male
        selectedGender = Gender.MALE
        updateGenderButtonStates()
    }
    
    private fun updateGenderButtonStates() {
        binding.maleButton.isChecked = selectedGender == Gender.MALE
        binding.femaleButton.isChecked = selectedGender == Gender.FEMALE
    }
    
    private fun setupSeatGrid() {
        trip?.let { currentTrip ->
            adapter = SeatAdapter(currentTrip.seats) { seat ->
                handleSeatClick(seat)
            }
            
            // Use GridLayoutManager with 2 columns for vertical bus layout
            // Left column: single seats (odd numbers: 1, 3, 5, 7, etc.)
            // Right column: paired seats (even numbers: 2, 4, 6, 8, etc.)
            // This creates a vertical bus view similar to real bus layout
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            binding.seatRecyclerView.layoutManager = gridLayoutManager
            binding.seatRecyclerView.adapter = adapter
        }
    }
    
    private fun handleSeatClick(seat: Seat) {
        trip?.let { currentTrip ->
            when (seat.status) {
                SeatStatus.AVAILABLE -> {
                    // Check if gender is selected
                    if (selectedGender == null) {
                        Toast.makeText(requireContext(), R.string.select_gender_first, Toast.LENGTH_SHORT).show()
                        return
                    }
                    
                    // Check if seat can be selected with this gender (adjacent seat rule)
                    if (!currentTrip.canSelectSeatWithGender(seat.number, selectedGender!!)) {
                        showGenderWarningDialog()
                        return
                    }
                    
                    // Select seat with gender
                    if (currentTrip.selectSeatWithGender(seat.number, selectedGender!!)) {
                        adapter?.notifyItemChanged(currentTrip.seats.indexOf(seat))
                        updateSelectedSeatsSummary()
                    } else {
                        Toast.makeText(requireContext(), "Seat could not be selected", Toast.LENGTH_SHORT).show()
                    }
                }
                SeatStatus.SELECTED -> {
                    // Deselect seat
                    val seatToDeselect = currentTrip.seats.find { it.number == seat.number }
                    seatToDeselect?.let {
                        it.status = SeatStatus.AVAILABLE
                        it.gender = null
                    }
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
                binding.selectedSeatsTextView.text = getString(R.string.no_seats_selected_summary)
                binding.totalPriceTextView.text = "0 TL"
            } else {
                val seatNumbers = selectedSeats.map { it.number }.sorted()
                val genderText = selectedSeats.firstOrNull()?.gender?.let {
                    if (it == Gender.MALE) getString(R.string.male_short) else getString(R.string.female_short)
                } ?: ""
                binding.selectedSeatsTextView.text = "${seatNumbers.joinToString(", ")} ($genderText)"
                val totalPrice = currentTrip.price * selectedSeats.size
                binding.totalPriceTextView.text = "$totalPrice TL"
            }
        }
    }
    
    private fun showGenderWarningDialog() {
        val dialogBinding = DialogGenderWarningBinding.inflate(LayoutInflater.from(requireContext()))
        
        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        
        dialogBinding.dialogTitle.text = getString(R.string.error)
        dialogBinding.dialogMessage.text = getString(R.string.gender_conflict_error)
        
        dialogBinding.dialogButton.setOnClickListener {
            dialog.dismiss()
        }
        
        // Make dialog background transparent and add rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.5f)
        dialog.show()
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
            
            // Check if all selected seats have gender assigned
            val seatsWithoutGender = selectedSeats.filter { it.gender == null }
            if (seatsWithoutGender.isNotEmpty()) {
                Toast.makeText(requireContext(), R.string.select_gender_first, Toast.LENGTH_SHORT).show()
                return
            }
            
            // Navigate to confirmation screen
            val seatNumbers = selectedSeats.map { it.number }
            val intent = ConfirmationActivity.newIntent(
                requireContext(),
                currentTrip.id,
                seatNumbers
            )
            startActivity(intent)
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

