package com.example.readtracker.android.di

import android.content.Context
import com.example.readtracker.android.ReadTrackerApp
import com.example.readtracker.android.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        PresentationModule::class
    ]
)
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory{

        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}