package com.example.readtracker.android.presentation.bookListScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.Intent
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.Label
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenStore.State
import javax.inject.Inject

interface BookListScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data class ClickBook(val bookId: String) : Intent
        data object ClickBack : Intent
    }

    data object State

    sealed interface Label {
        data class ClickBook(val bookId: String) : Label
        data object ClickBack : Label
    }
}

class BookListScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {
    fun create(): BookListScreenStore =
        object : BookListScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "BookListScreenStore",
            initialState = State,
            executorFactory = ::ExecutorImpl,
            reducer = NoOpReducer
        ) {}

    private object NoOpReducer : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State = this
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickBack -> publish(Label.ClickBack)
                is Intent.ClickBook -> publish(Label.ClickBook(intent.bookId))
            }
        }
    }
}

private typealias Msg = Nothing