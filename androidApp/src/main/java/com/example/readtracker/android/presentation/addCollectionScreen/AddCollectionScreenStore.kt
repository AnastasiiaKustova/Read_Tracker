package com.example.readtracker.android.presentation.addCollectionScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.bookCollection.AddCollectionInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.useCases.AddCollectionUseCase
import com.example.readtracker.android.domain.useCases.GetBooksByIdsUseCase
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenStore.Intent
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenStore.Label
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenStore.State
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenStoreFactory.Msg.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface AddCollectionScreenStore  : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ClickSave(val addCollectionInput: AddCollectionInput) : Intent
        data class ClickRemoveBook(val id: String) : Intent
        data class UpdateSelectedBooks(val ids: Set<String>) : Intent
        data object ClickAddBooks : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val selectedBooks: Set<Book>
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickSave : Label
        data object ClickAddBooks : Label
    }
}

class AddCollectionScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val addCollectionUseCase: AddCollectionUseCase,
    private val getBooksByIdsUseCase: GetBooksByIdsUseCase,
) {

    fun create(): AddCollectionScreenStore =
        object : AddCollectionScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddCollectionScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val selectedBooks: Set<Book>) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val selectedBooks: Set<Book>) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val selectedBooks = emptySet<Book>()
                    dispatch(Action.ScreenLoaded(selectedBooks))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.selectedBooks))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    Msg.ScreenLoaded(action.selectedBooks))
                Action.ScreenError -> dispatch(
                    Msg.ScreenError)
                Action.ScreenLoading -> dispatch(
                    Msg.ScreenLoading)
            }
        }
        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickSave -> {
                    scope.launch {
                        addCollectionUseCase(intent.addCollectionInput)
                        publish(Label.ClickSave)
                    }
                }

                Intent.ClickAddBooks -> {
                    publish(Label.ClickAddBooks)
                }

                is Intent.UpdateSelectedBooks -> {
                    scope.launch {
                        val selectedBooks = getBooksByIdsUseCase(intent.ids)
                        dispatch(ScreenLoaded(selectedBooks))
                    }
                }

                is Intent.ClickRemoveBook -> {
                    val currentState = state().screenState

                    if (currentState is State.ScreenState.Loaded) {
                        val updatedBooks = currentState.selectedBooks.filter { it.id != intent.id }.toSet()
                        dispatch(ScreenLoaded(updatedBooks))
                    }
                }
            }
        }
    }
}