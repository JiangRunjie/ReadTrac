package com.readtrac.readtrac.di

import com.readtrac.readtrac.model.RecommendationEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides recommendation components
 * 
 * This module provides instances related to the book recommendation feature.
 */
@Module
@InstallIn(SingletonComponent::class)
object RecommendationModule {
    
    /**
     * Provides the recommendation engine implementation
     * 
     * @return An instance of RecommendationEngine
     */
    @Provides
    @Singleton
    fun provideRecommendationEngine(): RecommendationEngine {
        return RecommendationEngine()
    }
}