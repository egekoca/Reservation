package com.sorareservation.ui.reservationlist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity

/**
 * Activity for displaying user's reservations
 */
class ReservationListActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        return ReservationListFragment.newInstance()
    }
    
    companion object {
        /**
         * Create intent for ReservationListActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, ReservationListActivity::class.java)
        }
    }
}

