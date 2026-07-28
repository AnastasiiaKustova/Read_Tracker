package com.example.readtracker.android.presentation.mainScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Intent
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Label
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.State
import javax.inject.Inject

interface MainScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {

        data object ClickCloseApp : Intent

    }

    data object State

    sealed interface Label {

        data object ClickCloseApp : Label

    }
}

class MainScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): MainScreenStore =
        object : MainScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainScreenStore",
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
                Intent.ClickCloseApp -> publish(Label.ClickCloseApp)
            }
        }
    }
}

private typealias Msg = Nothing