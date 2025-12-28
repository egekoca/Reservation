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
    private var leftAdapter: SeatAdapter? = null
    private var rightAdapter: SeatAdapter? = null
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
            val context = context ?: return
            val activity = activity ?: return
            Toast.makeText(context, R.string.trip_not_found, Toast.LENGTH_SHORT).show()
            activity.finish()
            return
        }
        
        // Restore selected seats from saved state
        savedInstanceState?.let {
            val selectedSeats = it.getIntegerArrayList("selected_seats")
            selectedSeats?.forEach { seatNumber ->
                trip?.selectSeats(listOf(seatNumber))
            }
        }
        
        setupToolbar()
        setupTripInfo()
        setupGenderSelection()
        setupSeatGrid()
        setupBookButton()
        updateSelectedSeatsSummary()
    }
    
    private fun setupToolbar() {
        val context = context ?: return
        val activity = activity ?: return
        if (activity !is androidx.appcompat.app.AppCompatActivity) return
        
        try {
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.title = getString(R.string.trip_detail_title)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            val previousGender = selectedGender
            selectedGender = Gender.MALE
            updateGenderButtonStates()
            
            // If gender changed, clear selected seats to avoid conflicts
            if (previousGender != null && previousGender != selectedGender) {
                trip?.clearSelectedSeats()
                leftAdapter?.notifyDataSetChanged()
                rightAdapter?.notifyDataSetChanged()
                updateSelectedSeatsSummary()
            }
        }
        
        binding.femaleButton.setOnClickListener {
            val previousGender = selectedGender
            selectedGender = Gender.FEMALE
            updateGenderButtonStates()
            
            // If gender changed, clear selected seats to avoid conflicts
            if (previousGender != null && previousGender != selectedGender) {
                trip?.clearSelectedSeats()
                leftAdapter?.notifyDataSetChanged()
                rightAdapter?.notifyDataSetChanged()
                updateSelectedSeatsSummary()
            }
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
            // Split seats into left (2-seat pairs) and right (single seats)
            // Layout: [1,2] | aisle | [3] | [4,5] | aisle | [6] | ...
            val leftSeats = mutableListOf<Seat>()
            val rightSeats = mutableListOf<Seat>()
            
            currentTrip.seats.forEach { seat ->
                val seatNumber = seat.number
                val positionInRow = (seatNumber - 1) % 3
                
                when (positionInRow) {
                    0, 1 -> leftSeats.add(seat) // First and second seats in row (left side)
                    2 -> rightSeats.add(seat)   // Third seat in row (right side)
                }
            }
            
            // Create adapters for left and right sides
            val leftAdapter = SeatAdapter(leftSeats) { seat ->
                handleSeatClick(seat)
            }
            
            val rightAdapter = SeatAdapter(rightSeats) { seat ->
                handleSeatClick(seat)
            }
            
            // Setup left RecyclerView (2 columns for paired seats)
            val context = context ?: return
            val leftGridLayoutManager = GridLayoutManager(context, 2)
            binding.leftSeatRecyclerView.layoutManager = leftGridLayoutManager
            binding.leftSeatRecyclerView.adapter = leftAdapter
            
            // Setup right RecyclerView (1 column for single seats)
            val rightLinearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            binding.rightSeatRecyclerView.layoutManager = rightLinearLayoutManager
            binding.rightSeatRecyclerView.adapter = rightAdapter
            
            // Store adapters
            this.leftAdapter = leftAdapter
            this.rightAdapter = rightAdapter
        }
    }
    
    /**
     * Update seat in the appropriate adapter (left or right)
     */
    private fun updateSeatInAdapter(seat: Seat) {
        val seatNumber = seat.number
        val positionInRow = (seatNumber - 1) % 3
        
        when (positionInRow) {
            0, 1 -> {
                // Left side seat
                leftAdapter?.let { adapter ->
                    val position = adapter.seats.indexOfFirst { it.number == seat.number }
                    if (position >= 0) {
                        adapter.notifyItemChanged(position)
                    }
                }
            }
            2 -> {
                // Right side seat
                rightAdapter?.let { adapter ->
                    val position = adapter.seats.indexOfFirst { it.number == seat.number }
                    if (position >= 0) {
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }
    }
    
    private fun handleSeatClick(seat: Seat) {
        val context = context ?: return
        if (!isAdded) return
        
        trip?.let { currentTrip ->
            when (seat.status) {
                SeatStatus.AVAILABLE -> {
                    // Check if gender is selected
                    if (selectedGender == null) {
                        Toast.makeText(context, R.string.select_gender_first, Toast.LENGTH_SHORT).show()
                        return
                    }
                    
                    // Check if seat can be selected with this gender (adjacent seat rule)
                    if (!currentTrip.canSelectSeatWithGender(seat.number, selectedGender!!)) {
                        showGenderWarningDialog()
                        return
                    }
                    
                    // Select seat with gender
                    if (currentTrip.selectSeatWithGender(seat.number, selectedGender!!)) {
                        updateSeatInAdapter(seat)
                        updateSelectedSeatsSummary()
                    } else {
                        Toast.makeText(context, R.string.seat_selection_failed, Toast.LENGTH_SHORT).show()
                    }
                }
                SeatStatus.SELECTED -> {
                    // Deselect seat
                    val seatToDeselect = currentTrip.seats.find { it.number == seat.number }
                    seatToDeselect?.let {
                        it.status = SeatStatus.AVAILABLE
                        it.gender = null
                    }
                    updateSeatInAdapter(seat)
                    updateSelectedSeatsSummary()
                }
                SeatStatus.OCCUPIED -> {
                    // Show error message for already booked seat
                    Toast.makeText(context, R.string.seat_already_booked, Toast.LENGTH_SHORT).show()
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
        val context = context ?: return
        if (!isAdded) return
        
        val dialogBinding = DialogGenderWarningBinding.inflate(LayoutInflater.from(context))
        
        val dialog = AlertDialog.Builder(context, R.style.AlertDialogTheme)
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
        val context = context ?: return
        if (!isAdded) return
        
        trip?.let { currentTrip ->
            val selectedSeats = currentTrip.getSelectedSeats()
            
            if (selectedSeats.isEmpty()) {
                Toast.makeText(context, R.string.no_seats_selected, Toast.LENGTH_SHORT).show()
                return
            }
            
            // Check if all selected seats have gender assigned
            val seatsWithoutGender = selectedSeats.filter { it.gender == null }
            if (seatsWithoutGender.isNotEmpty()) {
                Toast.makeText(context, R.string.select_gender_first, Toast.LENGTH_SHORT).show()
                return
            }
            
            // Navigate to confirmation screen
            val seatNumbers = selectedSeats.map { it.number }
            val gender = selectedSeats.firstOrNull()?.gender // All selected seats should have same gender
            val intent = ConfirmationActivity.newIntent(
                context,
                currentTrip.id,
                seatNumbers,
                gender
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

