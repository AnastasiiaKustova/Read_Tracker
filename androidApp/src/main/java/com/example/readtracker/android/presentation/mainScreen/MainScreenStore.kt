package com.example.readtracker.android.presentation.mainScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Intent
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.Label
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore.State
import javax.inject.Inject

interface MainScreenStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object ClickAddBook : Intent
        data class ClickBook(val bookId: String) : Intent
        data class ClickCollection(val collectionId: String) : Intent
        data class ClickBookStatus(val bookStatus: BookStatus) : Intent
    }

    data object State

    sealed interface Label {
        data object ClickAddBook : Label
        data class ClickBook(val bookId: String) : Label
        data class ClickCollection(val collectionId: String) : Label
        data class ClickBookStatus(val bookStatus: BookStatus) : Label
    }
}

class MainScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {
    fun create(): MainScreenStore =
        object : MainScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainScreenStore",
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
                Intent.ClickAddBook -> publish(Label.ClickAddBook)
                is Intent.ClickBook -> publish(Label.ClickBook(intent.bookId))
                is Intent.ClickCollection -> publish(Label.ClickCollection(intent.collectionId))
                is Intent.ClickBookStatus -> publish(Label.ClickBookStatus(intent.bookStatus))
            }
        }
    }
}

private typealias Msg = Nothing