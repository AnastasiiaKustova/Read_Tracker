package com.example.readtracker.android.presentation.trackerScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.useCases.AddActivityUseCase
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.UpdateBookUseCase
import com.example.readtracker.android.presentation.trackerScreen.ReadTrackerScreenStore.Intent
import com.example.readtracker.android.presentation.trackerScreen.ReadTrackerScreenStore.Label
import com.example.readtracker.android.presentation.trackerScreen.ReadTrackerScreenStore.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ReadTrackerScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickBack : Intent
        data class ClickFinishRead(val newPage: Int, val durationMinutes: Int) : Intent
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
        data object ClickBack : Label
    }
}

class ReadTrackerScreenStoreFactory @Inject constructor(
    private val addActivityUseCase: AddActivityUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val storeFactory: StoreFactory,
) {

    fun create(bookId: String): ReadTrackerScreenStore =
        object : ReadTrackerScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ReadTrackerScreenStore",
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
        private val bookId: String,
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
                Intent.ClickBack -> publish(Label.ClickBack)
                is Intent.ClickFinishRead -> {
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
                                    addActivityUseCase(
                                        AddStatsInput(
                                            book = updatedBook,
                                            pagesRead = intent.newPage - currentBook.currentPage,
                                            durationMinutes = intent.durationMinutes,
                                            statusChangedTo = if (currentBook.bookStatus == newStatus) null else newStatus
                                        )
                                    )
                                }
                                publish(Label.ClickBack)
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