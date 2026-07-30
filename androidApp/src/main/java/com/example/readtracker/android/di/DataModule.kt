package com.example.readtracker.android.di

import com.example.readtracker.android.data.repository.LitresRepositoryImpl
import com.example.readtracker.android.data.repository.MainScreenRepositoryImpl
import com.example.readtracker.android.data.repository.NotesRepositoryImpl
import com.example.readtracker.android.domain.repository.LitresRepository
import com.example.readtracker.android.domain.repository.MainScreenRepository
import com.example.readtracker.android.domain.repository.NotesRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @[ApplicationScope Binds]
    fun bindMainScreenRepository(impl: MainScreenRepositoryImpl): MainScreenRepository

    @[ApplicationScope Binds]
    fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @[ApplicationScope Binds]
    fun bindLitresRepository(impl: LitresRepositoryImpl): LitresRepository

    companion object{
        @[Provides ApplicationScope] fun provideGson(): Gson = Gson()
    }
}