package com.example.tinkoff.presentation.applications

import android.app.Application
import com.example.tinkoff.presentation.applications.di.AppComponent
import com.example.tinkoff.presentation.applications.di.DaggerAppComponent
import timber.log.Timber

class ApplicationController : Application() {
    private lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }
}
