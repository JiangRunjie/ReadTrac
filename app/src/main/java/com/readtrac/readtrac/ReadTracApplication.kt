package com.readtrac.readtrac

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom Application class for ReadTrac
 * 
 * This class is annotated with @HiltAndroidApp to enable dependency injection
 * with Hilt throughout the application. Hilt will automatically generate the
 * necessary components and provide dependencies where they are needed.
 */
@HiltAndroidApp
class ReadTracApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Additional initialization can be placed here
    }
}