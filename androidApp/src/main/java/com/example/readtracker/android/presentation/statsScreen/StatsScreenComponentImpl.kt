package com.example.readtracker.android.presentation.statsScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.readtracker.android.core.componentScope

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch

class StatsScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: StatsScreenStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,
) : StatsScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect{
                when(it){

                    else -> {}
                }
            }
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
        ): StatsScreenComponentImpl
    }
}