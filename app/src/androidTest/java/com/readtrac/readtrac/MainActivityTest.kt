package com.readtrac.readtrac

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Instrumented test for verifying the launch of the MainActivity
 * 
 * This test checks that the app's UI starts correctly and displays the home screen
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunch_homeScreenDisplayed() {
        // Check that the Home Screen is displayed with its title
        composeTestRule.onNodeWithText("ReadTrac Home").assertIsDisplayed()
    }
}