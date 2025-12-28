package com.sorareservation.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentAdminPanelBinding
import com.sorareservation.model.Trip
import com.sorareservation.ui.triplist.TripAdapter
import java.util.*

/**
 * Fragment for admin panel (add/delete trips)
 */
class AdminPanelFragment : Fragment() {
    
    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!
    
    private var adapter: TripAdapter? = null
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    
    companion object {
        /**
         * Create new instance of AdminPanelFragment
         */
        fun newInstance(): AdminPanelFragment {
            return AdminPanelFragment()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check if user is admin
        if (!SeferLab.isCurrentUserAdmin()) {
            Toast.makeText(requireContext(), "Access denied. Admin only.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }
        
        // Restore saved state
        savedInstanceState?.let {
            binding.departureCityEditText.setText(it.getString("departure", ""))
            binding.arrivalCityEditText.setText(it.getString("arrival", ""))
            binding.tripPriceEditText.setText(it.getString("price", ""))
            binding.totalSeatsEditText.setText(it.getString("seats", "45"))
        }
        
        setupDatePicker()
        setupTimePicker()
        setupAddButton()
        setupTripList()
    }
    
    private fun setupDatePicker() {
        binding.tripDateEditText.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    val dateStr = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    binding.tripDateEditText.setText(dateStr)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }
    }
    
    private fun setupTimePicker() {
        binding.tripTimeEditText.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val timeStr = String.format("%02d:%02d", hourOfDay, minute)
                    binding.tripTimeEditText.setText(timeStr)
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }
    }
    
    private fun setupAddButton() {
        binding.addTripButton.setOnClickListener {
            attemptAddTrip()
        }
    }
    
    private fun attemptAddTrip() {
        val departureCity = binding.departureCityEditText.text.toString().trim()
        val arrivalCity = binding.arrivalCityEditText.text.toString().trim()
        val dateStr = binding.tripDateEditText.text.toString().trim()
        val timeStr = binding.tripTimeEditText.text.toString().trim()
        val priceStr = binding.tripPriceEditText.text.toString().trim()
        val seatsStr = binding.totalSeatsEditText.text.toString().trim()
        
        if (departureCity.isEmpty() || arrivalCity.isEmpty() || dateStr.isEmpty() || 
            timeStr.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val price = priceStr.toDoubleOrNull()
        val seats = seatsStr.toIntOrNull()
        
        if (price == null || price <= 0) {
            Toast.makeText(requireContext(), "Invalid price", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (seats == null || seats <= 0) {
            Toast.makeText(requireContext(), "Invalid seat count", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Combine date and time
        val tripDateTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
            set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val newTrip = Trip(
            departureCity = departureCity,
            arrivalCity = arrivalCity,
            departureDate = tripDateTime.time,
            departureTime = timeStr,
            price = price,
            totalSeats = seats
        )
        
        val success = SeferLab.addTrip(newTrip)
        
        if (success) {
            Toast.makeText(requireContext(), R.string.trip_added, Toast.LENGTH_SHORT).show()
            // Clear form
            binding.departureCityEditText.setText("")
            binding.arrivalCityEditText.setText("")
            binding.tripDateEditText.setText("")
            binding.tripTimeEditText.setText("")
            binding.tripPriceEditText.setText("")
            binding.totalSeatsEditText.setText("45")
            // Refresh trip list
            setupTripList()
        } else {
            Toast.makeText(requireContext(), "Failed to add trip", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupTripList() {
        val trips = SeferLab.getTrips()
        
        adapter = TripAdapter(trips) { trip ->
            deleteTrip(trip)
        }
        
        binding.tripRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tripRecyclerView.adapter = adapter
    }
    
    private fun deleteTrip(trip: Trip) {
        val success = SeferLab.deleteTrip(trip.id)
        
        if (success) {
            Toast.makeText(requireContext(), R.string.trip_deleted, Toast.LENGTH_SHORT).show()
            setupTripList() // Refresh list
        } else {
            Toast.makeText(requireContext(), "Failed to delete trip", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("departure", binding.departureCityEditText.text.toString())
        outState.putString("arrival", binding.arrivalCityEditText.text.toString())
        outState.putString("price", binding.tripPriceEditText.text.toString())
        outState.putString("seats", binding.totalSeatsEditText.text.toString())
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

