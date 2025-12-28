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
 * Abstract base activity that hosts a single fragment
 * All activities in this app should extend this class
 */
abstract class SingleFragmentActivity : AppCompatActivity() {
    
    /**
     * Override this method to provide the fragment that should be displayed
     */
    protected abstract fun createFragment(): Fragment
    
    /**
     * Override this method to hide bottom navigation if needed (e.g., Login, Register)
     */
    protected open fun shouldShowBottomNavigation(): Boolean {
        return true
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        
        // Setup bottom navigation
        setupBottomNavigation()
        
        // Get fragment manager and check if fragment already exists
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        
        // If fragment doesn't exist, create it
        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    
    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fragmentContainer = findViewById<View>(R.id.fragment_container)
        
        if (bottomNav == null || !shouldShowBottomNavigation()) {
            bottomNav?.visibility = View.GONE
            // Remove margin when bottom nav is hidden
            fragmentContainer?.let {
                (it.layoutParams as? androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)?.bottomMargin = 0
            }
            return
        }
        
        // Add bottom margin to fragment container to avoid overlap with bottom navigation
        fragmentContainer?.let {
            val params = it.layoutParams as? androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
            // Bottom navigation height is typically 56dp
            params?.bottomMargin = (56 * resources.displayMetrics.density).toInt()
            it.layoutParams = params
        }
        
        // Show/hide admin menu item based on user role
        val adminMenuItem = bottomNav.menu.findItem(R.id.nav_admin)
        adminMenuItem?.isVisible = SeferLab.isCurrentUserAdmin()
        
        // Set current selected item based on current activity
        when (this) {
            is TripListActivity -> bottomNav.selectedItemId = R.id.nav_home
            is ReservationListActivity -> bottomNav.selectedItemId = R.id.nav_reservations
            is AdminPanelActivity -> bottomNav.selectedItemId = R.id.nav_admin
            else -> {
                // For other activities (TripDetail, Confirmation), keep home selected
                // They are sub-screens of the home flow
                bottomNav.selectedItemId = R.id.nav_home
            }
        }
        
        // Handle navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (this !is TripListActivity) {
                        val intent = TripListActivity.newIntent(this)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_reservations -> {
                    if (this !is ReservationListActivity) {
                        val intent = ReservationListActivity.newIntent(this)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                R.id.nav_admin -> {
                    if (this !is AdminPanelActivity) {
                        val intent = AdminPanelActivity.newIntent(this)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}

