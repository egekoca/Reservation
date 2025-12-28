package com.sorareservation.ui.admin

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity

/**
 * Activity for admin panel (add/delete trips)
 */
class AdminPanelActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        return AdminPanelFragment.newInstance()
    }
    
    companion object {
        /**
         * Create intent for AdminPanelActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, AdminPanelActivity::class.java)
        }
    }
}

