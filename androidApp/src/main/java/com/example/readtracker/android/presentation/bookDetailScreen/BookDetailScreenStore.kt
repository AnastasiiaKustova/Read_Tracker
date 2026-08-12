package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookDetail
import com.example.readtracker.android.domain.entity.BookDetailMode
import com.example.readtracker.android.domain.entity.BookItem
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
                val bookDetail: BookDetail
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
    fun create(bookId: String?, bookItem: BookItem?, mode: BookDetailMode): BookDetailScreenStore =
        object : BookDetailScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "BookDetailScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(bookId, bookItem, mode),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val bookDetail: BookDetail) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val bookDetail: BookDetail) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl(
        private val bookId: String?,
        private val bookItem: BookItem?,
        private val mode: BookDetailMode
    ) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val bookDetail = when (mode){
                        BookDetailMode.VIEW -> {
                            val book = if (bookId == null) {throw Exception("Error") } else { getBookByIdUseCase(bookId) }
                            BookDetail(
                                book = book,
                                bookItem = null,
                                mode = mode
                            )
                        }
                        BookDetailMode.SEARCH -> {
                            BookDetail(
                                book = null,
                                bookItem = bookItem,
                                mode = mode
                            )
                        }
                    }

                    dispatch(Action.ScreenLoaded(bookDetail))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.bookDetail))
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
                                val bookDetail = currentScreenState.bookDetail
                                val currentBook = bookDetail.book ?: throw Exception("Error")

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

                                dispatch(Msg.ScreenLoaded(bookDetail = bookDetail.copy(book = updatedBook)))

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

                                val bookDetail = currentScreenState.bookDetail
                                val currentBook = bookDetail.book ?: throw Exception("Error")

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

                                dispatch(Msg.ScreenLoaded(bookDetail = bookDetail.copy(book = updatedBook)))

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
                    Msg.ScreenLoaded(action.bookDetail)
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
