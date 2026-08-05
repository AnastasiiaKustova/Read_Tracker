package com.example.readtracker.android.presentation.addBookScreen

import android.net.Uri
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Intent
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Label
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.State
import javax.inject.Inject


interface AddBookScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickSearchLitres : Intent
        data class ClickSaveBook(val title: String, val author: String, val pages: Int, val desc: String, val coverUri: Uri?) : Intent
    }

    data object State

    sealed interface Label {
        data object ClickSearchLitres : Label
        data class ClickSaveBook(val title: String, val author: String, val pages: Int, val desc: String, val coverUri: Uri?) : Label
    }
}

class AddBookScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
) {

    fun create(): AddBookScreenStore =
        object : AddBookScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddBookScreenStore",
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
                is Intent.ClickSaveBook -> {
                    publish(Label.ClickSaveBook(intent.title, intent.author, intent.pages, intent.desc, intent.coverUri))
                }
                Intent.ClickSearchLitres -> {
                    publish(Label.ClickSearchLitres)
                }
            }
        }
    }
}

private typealias Msg = Nothing