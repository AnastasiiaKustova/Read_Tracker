package com.example.readtracker.android.presentation.addNoteScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.useCases.AddNoteUseCase
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.GetTagsUseCase
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.Intent
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.Label
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface AddNoteScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ClickSave(val addNoteInput: AddNoteInput) : Intent
        data class UpdateSelectedBook(val id: String) : Intent
        data object ClickSelectBook : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val selectedBook: Book?,
                val allAvailableTags: Set<Tag>
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickSave : Label
        data object ClickSelectBook : Label
    }
}

class AddNoteScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val addNoteUseCase: AddNoteUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
) {

    fun create(): AddNoteScreenStore =
        object : AddNoteScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddNoteScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val selectedBook: Book?, val allAvailableTags: Set<Tag>) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val selectedBook: Book?, val allAvailableTags: Set<Tag>) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val allAvailableTags = getTagsUseCase()
                    dispatch(Action.ScreenLoaded(null, allAvailableTags))
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
                is Msg.ScreenLoaded -> copy(
                    screenState = State.ScreenState.Loaded(
                        msg.selectedBook,
                        msg.allAvailableTags
                    )
                )
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    Msg.ScreenLoaded(action.selectedBook, action.allAvailableTags)
                )

                Action.ScreenError -> dispatch(
                    Msg.ScreenError
                )

                Action.ScreenLoading -> dispatch(
                    Msg.ScreenLoading
                )
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickSave -> {
                    scope.launch {
                        addNoteUseCase(intent.addNoteInput)
                        publish(Label.ClickSave)
                    }
                }

                Intent.ClickSelectBook -> publish(Label.ClickSelectBook)
                is Intent.UpdateSelectedBook -> {
                    scope.launch {
                        val currentState = state().screenState
                        if (currentState is State.ScreenState.Loaded) {
                            val selectedBook = getBookByIdUseCase(intent.id)
                            dispatch(Msg.ScreenLoaded(selectedBook, currentState.allAvailableTags))
                        }
                    }
                }
            }
        }
    }
}