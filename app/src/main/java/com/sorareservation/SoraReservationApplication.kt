package com.sorareservation

import android.app.Application
import com.sorareservation.data.SeferLab

/**
 * Application class for SoraReservation
 * Initializes SeferLab with database context
 */
class SoraReservationApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize SeferLab with application context
        SeferLab.init(this)
    }
}

