package com.example.readtracker.android.presentation.bookListScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.useCases.GetBooksUseCase
import com.example.readtracker.android.domain.useCases.GetCollectionByIdUseCase
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.Intent
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.Label
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.Label.*
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface BookListScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class ClickBook(val bookId: String) : Intent
        data class ClickMultiSelectConfirm(val bookIds: Set<String>) : Intent
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
                val title: String,
                val books: Set<Book>,
                val mode: BookListMode
            ) : ScreenState
        }
    }

    sealed interface Label {
        data class ClickBook(val bookId: String) : Label
        data class ClickMultiSelectConfirm(val bookIds: Set<String>) : Label
        data object ClickBack : Label
    }
}

class BookListScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getBooksUseCase: GetBooksUseCase,
    private val getCollectionByIdUseCase: GetCollectionByIdUseCase
) {
    fun create(collectionId: String?, bookStatus: BookStatus?, mode: BookListMode): BookListScreenStore =
        object : BookListScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "BookListScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(collectionId = collectionId, bookStatus = bookStatus, mode = mode),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val title: String, val books: Set<Book>, val mode: BookListMode) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val title: String, val books: Set<Book>, val mode: BookListMode) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl(
        private val collectionId: String?,
        private val bookStatus: BookStatus?,
        private val mode: BookListMode
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val title = bookStatus?.title
                        ?: if (collectionId != null) {
                            val collection = getCollectionByIdUseCase(collectionId)
                            collection.title
                        } else if (mode != BookListMode.VIEW) {
                            ""
                        } else {
                        throw Exception("Некорректные параметры")
                    }
                    val books = getBooksUseCase(collectionId, bookStatus)
                    dispatch(Action.ScreenLoaded(
                        title = title,
                        books = books,
                        mode = mode))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.title, msg.books, msg.mode))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickBack -> publish(ClickBack)
                is Intent.ClickBook -> publish(ClickBook(intent.bookId))
                is Intent.ClickMultiSelectConfirm -> publish(ClickMultiSelectConfirm(intent.bookIds))
            }
        }
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    Msg.ScreenLoaded(action.title, action.books, action.mode))
                Action.ScreenError -> dispatch(
                    Msg.ScreenError)
                Action.ScreenLoading -> dispatch(
                    Msg.ScreenLoading)
            }
        }
    }
}