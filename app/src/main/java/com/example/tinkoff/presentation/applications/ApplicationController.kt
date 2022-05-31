package com.example.tinkoff.presentation.applications

import android.app.Application
import com.example.tinkoff.model.room.client.RoomClient
import timber.log.Timber

class ApplicationController : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        RoomClient.initDatabase(applicationContext)
    }
}
