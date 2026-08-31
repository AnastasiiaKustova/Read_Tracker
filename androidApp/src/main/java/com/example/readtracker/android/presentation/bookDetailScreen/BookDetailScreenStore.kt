package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.BookDetail
import com.example.readtracker.android.domain.entity.BookDetailMode
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.SearchCategoriesUseCase
import com.example.readtracker.android.domain.useCases.AddActivityUseCase
import com.example.readtracker.android.domain.useCases.UpdateBookUseCase
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Intent
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Label
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.State
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStoreFactory.Msg.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface BookDetailScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class ClickEditBook(val book: Book)  : Intent
        data class ClickChangeStatus(val newStatus: BookStatus) : Intent
        data class ClickChoose(val bookItem: BookItem) : Intent
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
        data class ClickEditBook(val book: Book) : Label
        data class ClickChoose(val bookItem: BookItem) : Label
    }
}

class BookDetailScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val searchCategoriesUseCase: SearchCategoriesUseCase,
    private val addActivityUseCase: AddActivityUseCase
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
                                categories = emptyList(),
                                mode = mode
                            )
                        }
                        BookDetailMode.SEARCH -> {
                            if (bookItem == null) {throw Exception("Error") }
                            val categories = if (bookItem.genresList != null) { searchCategoriesUseCase(bookItem.genresList) } else { emptyList() }

                            BookDetail(
                                book = null,
                                bookItem = bookItem,
                                categories = categories,
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
                is Intent.ClickEditBook -> publish(Label.ClickEditBook(intent.book))
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
                                    addActivityUseCase(
                                        AddStatsInput(
                                            book = updatedBook,
                                            pagesRead = newPage - currentBook.currentPage,
                                            durationMinutes = 0,
                                            statusChangedTo = intent.newStatus
                                        )
                                    )
                                }

                                dispatch(ScreenLoaded(bookDetail = bookDetail.copy(book = updatedBook)))

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
                                    addActivityUseCase(
                                        AddStatsInput(
                                            book = updatedBook,
                                            pagesRead = intent.newPage - currentBook.currentPage,
                                            durationMinutes = 0,
                                            statusChangedTo = if (currentBook.bookStatus == newStatus) null else newStatus
                                        )
                                    )
                                }

                                dispatch(ScreenLoaded(bookDetail = bookDetail.copy(book = updatedBook)))

                            } catch (e: Exception) {
                                dispatch(Msg.ScreenError)
                            }
                        }
                    }
                }

                is Intent.ClickChoose -> publish(Label.ClickChoose(intent.bookItem))
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
