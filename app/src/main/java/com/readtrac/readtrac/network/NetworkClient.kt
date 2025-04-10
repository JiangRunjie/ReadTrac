package com.readtrac.readtrac.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

/**
 * Generic network result class to handle success and error states
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorMessage: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

/**
 * Network client for handling API requests with error handling and offline support
 */
class NetworkClient {
    
    /**
     * Execute a network API call with proper error handling and offline support
     * 
     * This function wraps API calls to provide consistent error handling,
     * offline checking, and response parsing.
     * 
     * @param T The type of data expected in the response
     * @param apiCall A suspend function representing the API call to execute
     * @return A Flow emitting NetworkResult states
     */
    fun <T> executeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(NetworkResult.Success(it))
                } ?: emit(NetworkResult.Error("Response body is empty"))
            } else {
                emit(NetworkResult.Error(
                    "API call failed with code: ${response.code()}",
                    response.code()
                ))
            }
        } catch (e: IOException) {
            // Handle network connectivity issues
            emit(NetworkResult.Error("Network error: Check your internet connection"))
        } catch (e: Exception) {
            emit(NetworkResult.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}