package com.joist.textecho.di

import com.joist.textecho.analytics.AnalyticsTracker
import com.joist.textecho.analytics.AnalyticsTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(
        impl: AnalyticsTrackerImpl
    ): AnalyticsTracker
}

