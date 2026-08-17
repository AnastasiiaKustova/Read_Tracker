package com.example.readtracker.android.presentation.searchBookScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.useCases.SearchBooksUseCase
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Intent
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Label
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.Label.*
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.State
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenStore.State.ScreenState.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

interface SearchBookScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class ClickBook(val bookItem: BookItem) : Intent
        data object ClickBack : Intent
        data class ChangeQuery(val query: String) : Intent
    }

    data class State(
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val screenState: ScreenState = Initial
    ) {
        sealed interface ScreenState {
            data object Initial : ScreenState
            data object Error : ScreenState
            data class Loaded(val books: Set<BookItem>) : ScreenState
        }
    }

    sealed interface Label {
        data class ClickBook(val bookItem: BookItem) : Label
        data object ClickBack : Label
    }
}

class SearchBookScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val searchBooksUseCase: SearchBooksUseCase
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

        data class QueryChanged(val query: String) : Msg

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
                Msg.ScreenError -> copy(
                    isLoading = false,
                    screenState = Error
                )
                Msg.ScreenLoading -> copy(
                    isLoading = true
                )
                is Msg.ScreenLoaded -> copy(
                    isLoading = false,
                    screenState = Loaded(books = msg.books)
                )
                is Msg.QueryChanged -> copy(
                    searchQuery = msg.query,
                    isLoading = msg.query.isNotBlank(),
                    screenState = if (msg.query.isBlank()) Initial else screenState
                )
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        private var searchJob: Job? = null

        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickBack -> publish(ClickBack)
                is Intent.ClickBook -> publish(ClickBook(intent.bookItem))
                is Intent.ChangeQuery -> {
                    // 1. Сразу сохраняем введенный текст в стейт через Редюсер, чтобы буквы на экране не тормозили
                    dispatch(Msg.QueryChanged(intent.query))

                    // 2. Отменяем предыдущий фоновый запрос поиска, если пользователь продолжает быстро писать
                    searchJob?.cancel()

                    // 3. Запускаем новый ленивый поиск с De-bounce задержкой
                    searchJob = scope.launch {
                        if (intent.query.isNotBlank()) {
                            dispatch(Msg.ScreenLoading) // Включаем лоадер
                            delay(500.milliseconds) // Ждем 500 мс. Если пользователь нажмет еще букву, корутина отменится на этой строке!

                            // Вызываем наш UseCase каталога
                            val foundBooks = searchBooksUseCase(intent.query)
                            dispatch(Msg.ScreenLoaded(books = foundBooks))
                        } else {
                            dispatch(Msg.ScreenLoaded(books = emptySet()))
                        }
                    }
                }
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