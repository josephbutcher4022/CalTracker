package com.example.caltracker

import android.app.Application
import timber.log.Timber

class CalTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}