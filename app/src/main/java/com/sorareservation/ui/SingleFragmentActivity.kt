package com.sorareservation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sorareservation.R

/**
 * Abstract base activity that hosts a single fragment
 * All activities in this app should extend this class
 */
abstract class SingleFragmentActivity : AppCompatActivity() {
    
    /**
     * Override this method to provide the fragment that should be displayed
     */
    protected abstract fun createFragment(): Fragment
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        
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
}

