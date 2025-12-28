package com.sorareservation.ui.reservationlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.databinding.FragmentReservationListBinding
import com.sorareservation.model.Reservation

/**
 * Fragment for displaying user's reservations
 */
class ReservationListFragment : Fragment() {
    
    private var _binding: FragmentReservationListBinding? = null
    private val binding get() = _binding!!
    
    private var adapter: ReservationAdapter? = null
    
    companion object {
        /**
         * Create new instance of ReservationListFragment
         */
        fun newInstance(): ReservationListFragment {
            return ReservationListFragment()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        loadReservations()
    }
    
    private fun setupToolbar() {
        val activity = requireActivity() as androidx.appcompat.app.AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.title = getString(R.string.reservation_list_title)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ReservationAdapter(
            emptyList(),
            onShareClick = { reservation -> shareReservation(reservation) },
            onCancelClick = { reservation -> cancelReservation(reservation) }
        )
        
        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reservationRecyclerView.adapter = adapter
    }
    
    private fun loadReservations() {
        val reservations = SeferLab.getReservationsForCurrentUser()
        
        if (reservations.isEmpty()) {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.reservationRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTextView.visibility = View.GONE
            binding.reservationRecyclerView.visibility = View.VISIBLE
            
            adapter = ReservationAdapter(
                reservations,
                onShareClick = { reservation -> shareReservation(reservation) },
                onCancelClick = { reservation -> cancelReservation(reservation) }
            )
            binding.reservationRecyclerView.adapter = adapter
        }
    }
    
    private fun shareReservation(reservation: Reservation) {
        // Implicit Intent for sharing reservation
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Bus Reservation")
            putExtra(Intent.EXTRA_TEXT, reservation.getSummary())
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share Reservation")
        
        if (shareIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(requireContext(), R.string.no_share_app, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun cancelReservation(reservation: Reservation) {
        val success = SeferLab.cancelReservation(reservation.id)
        
        if (success) {
            Toast.makeText(requireContext(), R.string.reservation_cancelled, Toast.LENGTH_SHORT).show()
            loadReservations() // Refresh the list
        } else {
            Toast.makeText(requireContext(), R.string.cancel_reservation_failed, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

