package com.sorareservation.ui.login

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.sorareservation.ui.SingleFragmentActivity

/**
 * Activity for user login
 */
class LoginActivity : SingleFragmentActivity() {
    
    override fun createFragment(): Fragment {
        return LoginFragment.newInstance()
    }
    
    companion object {
        /**
         * Create intent for LoginActivity
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}

