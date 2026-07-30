package com.example.readtracker.android.presentation.profileScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenStore.Intent
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenStore.State
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenStore.Label
import javax.inject.Inject

interface ProfileScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {

    }

    data object State

    sealed interface Label {

    }
}

class ProfileScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): ProfileScreenStore =
        object : ProfileScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileScreenStore",
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