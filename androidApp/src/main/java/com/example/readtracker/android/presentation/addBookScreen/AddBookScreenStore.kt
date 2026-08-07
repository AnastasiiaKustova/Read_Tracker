package com.example.readtracker.android.presentation.addBookScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.AddBookInput
import com.example.readtracker.android.domain.useCases.AddBookUseCase
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Intent
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Label
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.State
import kotlinx.coroutines.launch
import javax.inject.Inject


interface AddBookScreenStore: Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickSearchLitres : Intent
        data class ClickSaveBook(val addBookInput: AddBookInput) : Intent
    }

    data object State

    sealed interface Label {
        data object ClickSearchLitres : Label
        data object ClickSaveBook : Label
    }
}

class AddBookScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val addBookUseCase: AddBookUseCase
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
                    scope.launch {
                        addBookUseCase(intent.addBookInput)
                        publish(Label.ClickSaveBook)
                    }
                }
                Intent.ClickSearchLitres -> {
                    publish(Label.ClickSearchLitres)
                }
            }
        }
    }
}

private typealias Msg = Nothing