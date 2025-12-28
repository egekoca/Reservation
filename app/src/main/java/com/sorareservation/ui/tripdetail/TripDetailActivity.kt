package com.sorareservation.ui.tripdetail

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity
import java.util.UUID

/**
 * Activity for displaying trip details and seat selection
 */
class TripDetailActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        val tripId = intent.getSerializableExtra(EXTRA_TRIP_ID) as? UUID
        return TripDetailFragment.newInstance(tripId)
    }
    
    companion object {
        private const val EXTRA_TRIP_ID = "trip_id"
        
        /**
         * Create intent for TripDetailActivity
         */
        fun newIntent(context: Context, tripId: UUID): Intent {
            return Intent(context, TripDetailActivity::class.java).apply {
                putExtra(EXTRA_TRIP_ID, tripId)
            }
        }
    }
}

