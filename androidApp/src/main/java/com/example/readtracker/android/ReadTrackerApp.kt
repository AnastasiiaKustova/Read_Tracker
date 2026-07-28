package com.example.readtracker.android

import android.app.Application
import com.example.readtracker.android.di.ApplicationComponent
import com.example.readtracker.android.di.DaggerApplicationComponent

class ReadTrackerApp: Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}