package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Intent
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.Label
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore.State
import javax.inject.Inject

interface BookDetailScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickEditBook : Intent
        data object ClickChangeStatus : Intent
        data object ClickUpdatePage : Intent
    }

    data object State

    sealed interface Label {
        data object ClickEditBook : Label
        data object ClickChangeStatus : Label
        data object ClickUpdatePage : Label
    }
}

class BookDetailScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {
    fun create(): BookDetailScreenStore =
        object : BookDetailScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "BookDetailScreenStore",
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
                Intent.ClickEditBook -> publish(Label.ClickEditBook)
                Intent.ClickChangeStatus -> publish(Label.ClickChangeStatus)
                Intent.ClickUpdatePage -> publish(Label.ClickUpdatePage)
            }
        }
    }
}

private typealias Msg = Nothing