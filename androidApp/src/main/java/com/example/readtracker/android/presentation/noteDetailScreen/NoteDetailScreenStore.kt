package com.example.readtracker.android.presentation.noteDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.GetNoteByIdUseCase
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.Intent
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.Label
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NoteDetailScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickEdit : Intent
        data object ClickDelete : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val note: Note,
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickEdit : Label
        data object ClickDelete : Label
    }
}

class NoteDetailScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    fun create(noteId: String): NoteDetailScreenStore =
        object : NoteDetailScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "NoteDetailScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(noteId),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val note: Note) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val note: Note) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl(
        private val noteId: String
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val note = getNoteByIdUseCase(noteId)
                    dispatch(Action.ScreenLoaded(note))
                } catch (e: Exception) {
                    dispatch(Action.ScreenError)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                Msg.ScreenError -> copy(screenState = State.ScreenState.Error)
                Msg.ScreenLoading -> copy(screenState = State.ScreenState.Loading)
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.note))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(Msg.ScreenLoaded(action.note))
                Action.ScreenError -> dispatch(Msg.ScreenError)
                Action.ScreenLoading -> dispatch(Msg.ScreenLoading)
            }
        }
        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickEdit -> publish(Label.ClickEdit)
                Intent.ClickDelete -> publish(Label.ClickDelete)
            }
        }
    }
}