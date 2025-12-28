package com.sorareservation.ui.register

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity

/**
 * Activity for user registration
 */
class RegisterActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        return RegisterFragment.newInstance()
    }
    
    override fun shouldShowBottomNavigation(): Boolean {
        return false // Hide bottom navigation on register screen
    }
    
    companion object {
        /**
         * Create intent for RegisterActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }
}

