package com.readtrac.readtrac.util

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * Helper extension functions for Mockito to make it work better with Kotlin
 */

/**
 * Returns Mockito.any() as nullable type to avoid java.lang.IllegalStateException when 
 * null is returned.
 */
fun <T> anyObject(): T = Mockito.any<T>()

/**
 * A more Kotlin-friendly version of Mockito's when function
 */
fun <T> whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)