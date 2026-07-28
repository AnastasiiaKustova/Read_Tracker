package com.example.readtracker.android.di

import com.example.readtracker.android.data.repository.MainScreenRepositoryImpl
import com.example.readtracker.android.domain.repository.MainScreenRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindGameRepository(impl: MainScreenRepositoryImpl): MainScreenRepository

    companion object{
        @[Provides ApplicationScope] fun provideGson(): Gson = Gson()
    }
}