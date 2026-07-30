package com.example.readtracker.android.presentation.statsScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.Intent
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.State
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.Label
import javax.inject.Inject

interface StatsScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {

    }

    data object State

    sealed interface Label {

    }
}

class StatsScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): StatsScreenStore =
        object : StatsScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "StatsScreenStore",
            initialState = State,
            executorFactory = ::ExecutorImpl,
            reducer = NoOpReducer
        ) {}

    private object NoOpReducer : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = this
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {

                else -> {}
            }
        }
    }
}

private typealias Msg = Nothing