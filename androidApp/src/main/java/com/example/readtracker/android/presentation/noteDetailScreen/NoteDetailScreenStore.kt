package com.example.readtracker.android.presentation.noteDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.Intent
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.Label
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.State
import javax.inject.Inject

interface NoteDetailScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickEdit : Intent
        data object ClickDelete : Intent
    }

    data object State

    sealed interface Label {
        data object ClickEdit : Label
        data object ClickDelete : Label
    }
}

class NoteDetailScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {
    fun create(): NoteDetailScreenStore =
        object : NoteDetailScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "NoteDetailScreenStore",
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
                Intent.ClickEdit -> publish(Label.ClickEdit)
                Intent.ClickDelete -> publish(Label.ClickDelete)
            }
        }
    }
}

private typealias Msg = Nothing