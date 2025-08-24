package com.example.caltracker

import android.app.Application
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree()) // Initialize Timber for logging
        Timber.d("MyApplication: Timber initialized")
    }
}