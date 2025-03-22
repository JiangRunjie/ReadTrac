package com.readtrac.readtrac

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Instrumented test for verifying the launch of the MainActivity
 * 
 * This test checks that the app's UI starts correctly and displays the home screen
 */
@HiltAndroidTest
class MainActivityTest {

    private val hiltRule = HiltAndroidRule(this)
    private val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val rules = RuleChain
        .outerRule(hiltRule)
        .around(composeRule)

    @Test
    fun testAppLaunch_homeScreenDisplayed() {
        // Check that the Home Screen is displayed with its title
        composeRule.onNodeWithText("ReadTrac Home").assertIsDisplayed()
    }
}