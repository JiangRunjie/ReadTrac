package com.readtrac.readtrac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.readtrac.readtrac.routes.AppNavigation
import com.readtrac.readtrac.ui.theme.ReadTracTheme

class MainActivity : ComponentActivity() {
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