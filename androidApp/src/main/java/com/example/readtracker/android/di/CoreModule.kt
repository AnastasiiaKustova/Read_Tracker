package com.example.readtracker.android.di

import com.example.readtracker.android.core.DateTimeManager
import dagger.Module
import dagger.Provides

@Module
interface CoreModule {

    companion object {
        @Provides
        @ApplicationScope
        fun provideDateTimeManager(): DateTimeManager = DateTimeManager()
    }
}