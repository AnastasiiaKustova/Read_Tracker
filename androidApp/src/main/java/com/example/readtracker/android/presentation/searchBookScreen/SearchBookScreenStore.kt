package com.example.readtracker.android.presentation.searchBookScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.BookItem
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.useCases.GetBooksUseCase
import com.example.readtracker.android.domain.useCases.GetCollectionByIdUseCase
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Intent
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Label
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Label.*
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SearchBookScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class ClickBook(val bookItem: BookItem) : Intent
        data object ClickBack : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val books: Set<BookItem>,
            ) : ScreenState
        }
    }

    sealed interface Label {
        data class ClickBook(val bookItem: BookItem) : Label
        data object ClickBack : Label
    }
}

class SearchBookScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {
    fun create(): SearchBookScreenStore =
        object : SearchBookScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SearchBookScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val books: Set<BookItem>) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val books: Set<BookItem>) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    dispatch(Action.ScreenLoaded(
                        books = emptySet()))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.books))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickBack -> publish(ClickBack)
                is Intent.ClickBook -> publish(ClickBook(intent.bookItem))
            }
        }
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    Msg.ScreenLoaded(action.books))
                Action.ScreenError -> dispatch(
                    Msg.ScreenError)
                Action.ScreenLoading -> dispatch(
                    Msg.ScreenLoading)
            }
        }
    }
}