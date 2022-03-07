package com.example.homework1

import android.app.Application
import timber.log.Timber

class ApplicationController : Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}