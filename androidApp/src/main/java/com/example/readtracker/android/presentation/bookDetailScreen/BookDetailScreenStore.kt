package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.UpdateBookUseCase
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Intent
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Label
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BookDetailScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickEditBook : Intent
        data class ClickChangeStatus(val newStatus: BookStatus) : Intent
        data class ClickUpdatePage(val newPage: Int) : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val book: Book
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickEditBook : Label
        data object ClickChangeStatus : Label
        data object ClickUpdatePage : Label
    }
}

class BookDetailScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase
) {
    fun create(bookId: String): BookDetailScreenStore =
        object : BookDetailScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "BookDetailScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(bookId),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val book: Book) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val book: Book) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl(
        private val bookId: String
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val book = getBookByIdUseCase(bookId)
                    dispatch(Action.ScreenLoaded(book))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.book))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickEditBook -> publish(Label.ClickEditBook)
                is Intent.ClickChangeStatus -> {
                    scope.launch {
                        val currentScreenState = state().screenState

                        if (currentScreenState is State.ScreenState.Loaded) {
                            try {
                                val currentBook = currentScreenState.book

                                val newPage = if (intent.newStatus == BookStatus.FINISHED ) {
                                    currentBook.totalPages
                                } else {
                                    currentBook.currentPage
                                }

                                val updatedBook = currentBook.copy(
                                    bookStatus = intent.newStatus,
                                    currentPage = newPage
                                )

                                withContext(Dispatchers.IO) {
                                    updateBookUseCase(updatedBook)
                                }

                                dispatch(Msg.ScreenLoaded(book = updatedBook))

                            } catch (e: Exception) {
                                dispatch(Msg.ScreenError)
                            }
                        }
                    }
                }

                is Intent.ClickUpdatePage -> {
                    scope.launch {
                        val currentScreenState = state().screenState

                        if (currentScreenState is State.ScreenState.Loaded) {
                            try {

                                val currentBook = currentScreenState.book

                                val newStatus =
                                    if (currentBook.totalPages == intent.newPage) {
                                        BookStatus.FINISHED
                                    } else {
                                        currentBook.bookStatus
                                    }

                                val updatedBook = currentBook.copy(
                                    currentPage = intent.newPage,
                                    bookStatus = newStatus
                                )

                                withContext(Dispatchers.IO) {
                                    updateBookUseCase(updatedBook)
                                }

                                dispatch(Msg.ScreenLoaded(book = updatedBook))

                            } catch (e: Exception) {
                                dispatch(Msg.ScreenError)
                            }
                        }
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    Msg.ScreenLoaded(action.book)
                )

                Action.ScreenError -> dispatch(
                    Msg.ScreenError
                )

                Action.ScreenLoading -> dispatch(
                    Msg.ScreenLoading
                )
            }
        }
    }
}
