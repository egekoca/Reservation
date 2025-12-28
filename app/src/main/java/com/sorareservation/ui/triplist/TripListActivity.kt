package com.sorareservation.ui.triplist

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity

/**
 * Activity for displaying list of available trips
 */
class TripListActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        return TripListFragment.newInstance()
    }
    
    companion object {
        /**
         * Create intent for TripListActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, TripListActivity::class.java)
        }
    }
}

