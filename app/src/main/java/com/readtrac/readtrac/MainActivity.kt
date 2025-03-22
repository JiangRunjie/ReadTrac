package com.readtrac.readtrac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.readtrac.readtrac.routes.AppNavigation
import com.readtrac.readtrac.ui.theme.ReadTracTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the ReadTrac application
 * 
 * This activity initializes the Compose UI and sets up the navigation
 * framework for the application. It serves as the container for all
 * other screens in the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Initializes the activity and sets up the Compose UI
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the data it most recently supplied in onSaveInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReadTracTheme {
                AppNavigation()
            }
        }
    }
}