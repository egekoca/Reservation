package com.sorareservation.ui.confirmation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.model.Gender
import com.sorareservation.ui.SingleFragmentActivity
import java.util.*
import java.util.UUID

/**
 * Activity for confirming reservation and payment
 */
class ConfirmationActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        val tripId = intent.getSerializableExtra(EXTRA_TRIP_ID) as? UUID
        val seatNumbers = intent.getIntegerArrayListExtra(EXTRA_SEAT_NUMBERS) ?: arrayListOf()
        val genderOrdinal = intent.getIntExtra(EXTRA_GENDER, -1)
        val gender = if (genderOrdinal >= 0) Gender.values()[genderOrdinal] else null
        return ConfirmationFragment.newInstance(tripId, seatNumbers, gender)
    }
    
    companion object {
        private const val EXTRA_TRIP_ID = "trip_id"
        private const val EXTRA_SEAT_NUMBERS = "seat_numbers"
        private const val EXTRA_GENDER = "gender"
        
        /**
         * Create intent for ConfirmationActivity
         */
        fun newIntent(context: Context, tripId: UUID, seatNumbers: List<Int>, gender: Gender?): Intent {
            return Intent(context, ConfirmationActivity::class.java).apply {
                putExtra(EXTRA_TRIP_ID, tripId)
                putIntegerArrayListExtra(EXTRA_SEAT_NUMBERS, ArrayList(seatNumbers))
                putExtra(EXTRA_GENDER, gender?.ordinal ?: -1)
            }
        }
    }
}

