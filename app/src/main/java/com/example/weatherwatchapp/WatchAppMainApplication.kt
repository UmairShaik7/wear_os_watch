package com.example.weatherwatchapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WatchAppMainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin {
            androidContext(this@WatchAppMainApplication)
            androidLogger()
            modules(listOf(repoModule, appModule))
        }
    }
}