package com.example.readtracker.android.presentation.noteScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.useCases.GetNotesUseCase
import com.example.readtracker.android.domain.useCases.GetTagsUseCase
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Intent
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Label
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Label.ClickAddNote
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.Label.ClickNote
import com.example.readtracker.android.presentation.noteScreen.NoteScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NoteScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickAddNote : Intent
        data class ClickNote(val noteId: String) : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val notes: Set<Note>,
                val tags: Set<Tag>,
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickAddNote : Label
        data class ClickNote(val noteId: String) : Label
    }
}

class NoteScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getNotesUseCase: GetNotesUseCase,
    private val getTagsUseCase: GetTagsUseCase,
) {

    fun create(): NoteScreenStore =
        object : NoteScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "NoteScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(
            val notes: Set<Note>,
            val tags: Set<Tag>,
        ) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(
            val notes: Set<Note>,
            val tags: Set<Tag>,
        ) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val notes = getNotesUseCase()
                    val tags = getTagsUseCase()
                    dispatch(Action.ScreenLoaded(notes = notes, tags = tags))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(notes = msg.notes, tags = msg.tags))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(Msg.ScreenLoaded(notes = action.notes, tags = action.tags))
                Action.ScreenError -> dispatch(Msg.ScreenError)
                Action.ScreenLoading -> dispatch(Msg.ScreenLoading)
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickNote -> {
                    // Теперь публикация Label отработает без рантайм конфликтов
                    publish(ClickNote(intent.noteId))
                }

                Intent.ClickAddNote -> publish(ClickAddNote)
            }
        }
    }
}