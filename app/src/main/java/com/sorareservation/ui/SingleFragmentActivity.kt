package com.sorareservation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sorareservation.R
import com.sorareservation.data.SeferLab
import com.sorareservation.ui.admin.AdminPanelActivity
import com.sorareservation.ui.reservationlist.ReservationListActivity
import com.sorareservation.ui.triplist.TripListActivity

/**
 * Tüm Activity'lerin temel sınıfı (Single Fragment Activity Pattern)
 * 
 * Bu sınıf, Activity'lerin sadece container görevi görmesini sağlar.
 * Tüm iş mantığı Fragment'lerde bulunur.
 * 
 * Kullanım: Tüm Activity'ler bu sınıftan türer ve createFragment() metodunu override eder.
 */
abstract class SingleFragmentActivity : AppCompatActivity() {
    
    /**
     * Fragment oluşturma metodu
     * Her Activity bu metodu override ederek kendi Fragment'ını döndürmelidir
     * 
     * @return Gösterilecek Fragment
     */
    protected abstract fun createFragment(): Fragment
    
    /**
     * Bottom navigation bar'ın gösterilip gösterilmeyeceğini belirler
     * Login ve Register ekranlarında false döndürülmelidir
     * 
     * @return true ise bottom navigation gösterilir, false ise gizlenir
     */
    protected open fun shouldShowBottomNavigation(): Boolean {
        return true
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Base layout'u yükle (FragmentContainerView + BottomNavigationView içerir)
        setContentView(R.layout.activity_fragment)
        
        // Bottom navigation bar'ı ayarla
        setupBottomNavigation()
        
        // Fragment manager'ı al ve Fragment'ın zaten var olup olmadığını kontrol et
        // (Ekran döndürme durumunda Fragment zaten mevcut olabilir)
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        
        // Eğer Fragment yoksa, yeni oluştur ve container'a ekle
        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    
    /**
     * Bottom navigation bar'ı ayarlar
     * 
     * Bu metod:
     * - Bottom navigation'ın görünürlüğünü kontrol eder
     * - Fragment container'a margin ekler (overlap önleme)
     * - Admin menü öğesini kullanıcı rolüne göre gösterir/gizler
     * - Mevcut Activity'ye göre seçili öğeyi ayarlar
     * - Navigation item tıklamalarını yönetir
     */
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fragmentContainer = findViewById<View>(R.id.fragment_container)
        
        // Eğer bottom navigation yoksa veya gösterilmemesi gerekiyorsa gizle
        if (bottomNav == null || !shouldShowBottomNavigation()) {
            bottomNav?.visibility = View.GONE
            // Bottom nav gizliyken margin'i kaldır
            fragmentContainer?.let {
                (it.layoutParams as? androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)?.bottomMargin = 0
            }
            return
        }
        
        // Fragment container'a alt margin ekle (bottom navigation ile çakışmayı önlemek için)
        fragmentContainer?.let {
            val params = it.layoutParams as? androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            // Bottom navigation yüksekliği genellikle 56dp'dir
            // dp'yi piksele çevir (density ile çarp)
            params?.bottomMargin = (56 * resources.displayMetrics.density).toInt()
            it.layoutParams = params
        }
        
        // Admin menü öğesini kullanıcı rolüne göre göster/gizle
        // Sadece admin kullanıcılar admin paneli menü öğesini görebilir
        val adminMenuItem = bottomNav.menu.findItem(R.id.nav_admin)
        adminMenuItem?.isVisible = SeferLab.isCurrentUserAdmin()
        
        // Mevcut Activity'ye göre seçili menü öğesini ayarla
        // Bu, kullanıcının hangi ekranda olduğunu görsel olarak gösterir
        when (this) {
            is TripListActivity -> bottomNav.selectedItemId = R.id.nav_home
            is ReservationListActivity -> bottomNav.selectedItemId = R.id.nav_reservations
            is AdminPanelActivity -> bottomNav.selectedItemId = R.id.nav_admin
            else -> {
                // Diğer Activity'ler için (TripDetail, Confirmation) home seçili kalır
                // Bunlar ana akışın alt ekranlarıdır
                bottomNav.selectedItemId = R.id.nav_home
            }
        }
        
        // Navigation item tıklamalarını yönet
        // Kullanıcı bir menü öğesine tıkladığında ilgili Activity'ye yönlendirilir
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Home (Trip List) seçildi
                    // Eğer zaten TripListActivity'de değilsek, oraya git
                    if (this !is TripListActivity) {
                        val intent = TripListActivity.newIntent(this)
                        // Activity stack'ini temizle ve yeni task başlat
                        // Bu, back stack'i düzgün yönetmek için önemlidir
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish() // Mevcut Activity'yi kapat
                    }
                    true // Tıklama işlendi
                }
                R.id.nav_reservations -> {
                    // My Reservations seçildi
                    if (this !is ReservationListActivity) {
                        val intent = ReservationListActivity.newIntent(this)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_admin -> {
                    // Admin Panel seçildi
                    if (this !is AdminPanelActivity) {
                        val intent = AdminPanelActivity.newIntent(this)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                else -> false // Bilinmeyen item, işlenmedi
            }
        }
    }
}

