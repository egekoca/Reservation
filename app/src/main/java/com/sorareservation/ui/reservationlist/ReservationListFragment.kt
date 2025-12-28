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
import com.sorareservation.ui.triplist.TripListActivity

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
        val context = context ?: return
        val activity = activity ?: return
        if (activity !is androidx.appcompat.app.AppCompatActivity) return
        
        try {
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.title = getString(R.string.reservation_list_title)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.setNavigationOnClickListener {
                // Navigate to Trip List (main menu) instead of just finishing
                val intent = TripListActivity.newIntent(context)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity.finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupRecyclerView() {
        val context = context ?: return
        adapter = ReservationAdapter(
            emptyList(),
            onShareClick = { reservation -> shareReservation(reservation) },
            onCancelClick = { reservation -> cancelReservation(reservation) }
        )
        
        binding.reservationRecyclerView.layoutManager = LinearLayoutManager(context)
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
    
    /**
     * Rezervasyonu başka uygulamalarla paylaşır (Implicit Intent kullanarak)
     * 
     * Bu metod, Android'in standart paylaşım sistemini kullanır.
     * Sistem, ACTION_SEND action'ını destekleyen tüm uygulamaları bulur
     * (WhatsApp, Gmail, SMS, Telegram, vb.) ve kullanıcıya seçenekler sunar.
     * 
     * @param reservation Paylaşılacak rezervasyon
     */
    private fun shareReservation(reservation: Reservation) {
        val context = context ?: return // Context null kontrolü
        if (!isAdded) return // Fragment hala ekrana ekli mi kontrol et
        
        // Implicit Intent oluştur (örtülü intent)
        // ACTION_SEND: "Paylaş" eylemi
        // type = "text/plain": Metin paylaşımı
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND // Paylaş eylemi
            type = "text/plain" // Paylaşılacak veri tipi: metin
            putExtra(Intent.EXTRA_SUBJECT, "My Bus Reservation") // Paylaşım konusu
            putExtra(Intent.EXTRA_TEXT, reservation.getSummary()) // Paylaşılacak metin (rezervasyon detayları)
        }
        
        // Chooser dialog oluştur
        // Kullanıcıya hangi uygulamayla paylaşmak istediğini sorar
        val chooserIntent = Intent.createChooser(shareIntent, "Share Reservation")
        
        // Sistemde paylaşma yapabilecek bir uygulama var mı kontrol et
        // resolveActivity() null dönerse, hiçbir uygulama bu action'ı desteklemiyor demektir
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            // Uygulama bulundu, chooser dialog'u göster
            startActivity(chooserIntent)
        } else {
            // Paylaşma yapabilecek uygulama yok, kullanıcıya bilgi ver
            Toast.makeText(context, R.string.no_share_app, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun cancelReservation(reservation: Reservation) {
        val context = context ?: return
        if (!isAdded) return
        
        val success = SeferLab.cancelReservation(reservation.id)
        
        if (success) {
            Toast.makeText(context, R.string.reservation_cancelled, Toast.LENGTH_SHORT).show()
            loadReservations() // Refresh the list
        } else {
            Toast.makeText(context, R.string.cancel_reservation_failed, Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

