package com.readtrac.readtrac.di

import com.readtrac.readtrac.network.BookApiService
import com.readtrac.readtrac.network.NetworkClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing network-related dependencies
 *
 * This module provides instances of network clients, service interfaces, and related
 * components needed for API communication.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * Base URL for the Google Books API
     * 
     * If the API endpoint needs to be updated in the future, change this constant.
     */
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"
    
    /**
     * Provides an instance of OkHttpClient with timeouts and interceptors
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Provides a Retrofit instance for API communication
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Provides the BookApiService implementation
     */
    @Provides
    @Singleton
    fun provideBookApiService(retrofit: Retrofit): BookApiService {
        return retrofit.create(BookApiService::class.java)
    }
    
    /**
     * Provides the NetworkClient for handling API requests
     */
    @Provides
    @Singleton
    fun provideNetworkClient(): NetworkClient {
        return NetworkClient()
    }
}