package com.example.readtracker.android.presentation.noteScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Intent
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Label
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.State
import javax.inject.Inject

interface NoteScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {
        data class ClickNote(val noteId: String) : Intent
    }

    data object State

    sealed interface Label {
        data class ClickNote(val noteId: String) : Label
    }
}

class NoteScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): NoteScreenStore =
        object : NoteScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "NoteScreenStore",
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
                is Intent.ClickNote -> {
                    // Теперь публикация Label отработает без рантайм конфликтов
                    publish(Label.ClickNote(intent.noteId))
                }
            }
        }
    }
}

private typealias Msg = Nothing