package com.sorareservation.ui.triplist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentTripListBinding
import com.sorareservation.model.Trip
import com.sorareservation.ui.admin.AdminPanelActivity
import com.sorareservation.ui.login.LoginActivity
import com.sorareservation.ui.reservationlist.ReservationListActivity
import com.sorareservation.ui.tripdetail.TripDetailActivity

/**
 * Fragment for displaying list of available trips
 */
class TripListFragment : Fragment() {
    
    private var _binding: FragmentTripListBinding? = null
    private val binding get() = _binding!!
    
    private var adapter: TripAdapter? = null
    private var allTrips: List<Trip> = emptyList()
    
    companion object {
        /**
         * Create new instance of TripListFragment
         */
        fun newInstance(): TripListFragment {
            return TripListFragment()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Restore saved state
        savedInstanceState?.let {
            binding.departureEditText.setText(it.getString("departure", ""))
            binding.arrivalEditText.setText(it.getString("arrival", ""))
        }
        
        setupToolbar()
        setupRecyclerView()
        setupSearchFilters()
        loadTrips()
    }
    
    private fun setupToolbar() {
        try {
            val activity = requireActivity()
            if (activity is androidx.appcompat.app.AppCompatActivity && binding.toolbar != null) {
                activity.setSupportActionBar(binding.toolbar)
                activity.supportActionBar?.title = getString(R.string.trip_list_title)
            }
        } catch (e: Exception) {
            // Toolbar setup failed, continue without toolbar
            e.printStackTrace()
        }
    }
    
    private fun setupRecyclerView() {
        binding.tripRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // Initialize adapter with empty list first
        adapter = TripAdapter(emptyList()) { trip ->
            navigateToTripDetail(trip.id)
        }
        binding.tripRecyclerView.adapter = adapter
    }
    
    private fun setupSearchFilters() {
        if (!isAdded || _binding == null) {
            return
        }
        
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isAdded && _binding != null) {
                    filterTrips()
                }
            }
        }
        
        binding.departureEditText.addTextChangedListener(textWatcher)
        binding.arrivalEditText.addTextChangedListener(textWatcher)
    }
    
    private fun loadTrips() {
        allTrips = SeferLab.getTrips()
        filterTrips()
    }
    
    private fun filterTrips() {
        if (!isAdded || _binding == null) {
            return
        }
        
        val departure = binding.departureEditText.text.toString().trim()
        val arrival = binding.arrivalEditText.text.toString().trim()
        
        val filteredTrips = if (departure.isEmpty() && arrival.isEmpty()) {
            allTrips
        } else {
            SeferLab.searchTrips(
                if (departure.isEmpty()) null else departure,
                if (arrival.isEmpty()) null else arrival
            )
        }
        
        // Update adapter with filtered trips
        adapter?.updateData(filteredTrips)
        
        // Show/hide empty state
        if (filteredTrips.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.tripRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.tripRecyclerView.visibility = View.VISIBLE
        }
    }
    
    private fun navigateToTripDetail(tripId: java.util.UUID) {
        val intent = TripDetailActivity.newIntent(requireContext(), tripId)
        startActivity(intent)
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.trip_list_menu, menu)
        
        // Show admin panel option only for admin users
        val adminMenuItem = menu.findItem(R.id.action_admin_panel)
        adminMenuItem?.isVisible = SeferLab.isCurrentUserAdmin()
        
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_my_reservations -> {
                navigateToReservations()
                true
            }
            R.id.action_admin_panel -> {
                navigateToAdminPanel()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun navigateToReservations() {
        val intent = ReservationListActivity.newIntent(requireContext())
        startActivity(intent)
    }
    
    private fun navigateToAdminPanel() {
        val intent = AdminPanelActivity.newIntent(requireContext())
        startActivity(intent)
    }
    
    private fun logout() {
        SeferLab.logout()
        val intent = LoginActivity.newIntent(requireContext())
        startActivity(intent)
        requireActivity().finish()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.let {
            outState.putString("departure", it.departureEditText.text.toString())
            outState.putString("arrival", it.arrivalEditText.text.toString())
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
