package com.example.readtracker.android.presentation.mainScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookCollection
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.Note
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.domain.useCases.GetBooksUseCase
import com.example.readtracker.android.domain.useCases.GetCollectionsUseCase
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Intent
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Label
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickAddBook : Intent
        data object ClickAddCollection : Intent
        data class ClickBook(val bookId: String) : Intent
        data class ClickCollection(val collectionId: String) : Intent
        data class ClickBookStatus(val bookStatus: BookStatus) : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val books: Set<Book>,
                val collections: Set<BookCollection>,
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickAddBook : Label
        data object ClickAddCollection : Label
        data class ClickBook(val bookId: String) : Label
        data class ClickCollection(val collectionId: String) : Label
        data class ClickBookStatus(val bookStatus: BookStatus) : Label
    }
}

class MainScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getBooksUseCase: GetBooksUseCase,
    private val getCollectionsUseCase: GetCollectionsUseCase
) {
    fun create(): MainScreenStore =
        object : MainScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(
            val books: Set<Book>,
            val collections: Set<BookCollection>,
        ) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(
            val books: Set<Book>,
            val collections: Set<BookCollection>,
        ) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl: CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val books = getBooksUseCase(
                        collectionId = null,
                        bookStatus = BookStatus.READING
                    )
                    val collections = getCollectionsUseCase()
                    dispatch(Action.ScreenLoaded(books = books, collections = collections))
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
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(books = msg.books, collections = msg.collections))
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(Msg.ScreenLoaded(books = action.books, collections = action.collections))
                Action.ScreenError -> dispatch(Msg.ScreenError)
                Action.ScreenLoading -> dispatch(Msg.ScreenLoading)
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickAddBook -> publish(Label.ClickAddBook)
                Intent.ClickAddCollection -> publish(Label.ClickAddCollection)
                is Intent.ClickBook -> publish(Label.ClickBook(intent.bookId))
                is Intent.ClickCollection -> publish(Label.ClickCollection(intent.collectionId))
                is Intent.ClickBookStatus -> publish(Label.ClickBookStatus(intent.bookStatus))
            }
        }
    }
}