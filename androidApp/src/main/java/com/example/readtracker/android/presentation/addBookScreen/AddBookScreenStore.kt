package com.example.readtracker.android.presentation.addBookScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.useCases.AddBookUseCase
import com.example.readtracker.android.domain.useCases.UpdateBookUseCase
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Intent
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.Label
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenStore.State
import com.example.readtracker.android.presentation.addBookScreen.Msg.*
import kotlinx.coroutines.launch
import javax.inject.Inject


interface AddBookScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data object ClickSearchLitres : Intent
        data class ClickSaveBook(val addBookInput: AddBookInput) : Intent
        data class ClickUpdateBook(val book: Book) : Intent
        data class LoadBookFromBase(val selectedBook: BookItem) : Intent
    }

    data class State (val loadedBook: BookItem? = null, val bookForUpdate: Book? = null)

    sealed interface Label {
        data object ClickSearchLitres : Label
        data object ClickSaveBook : Label
    }
}

class AddBookScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val addBookUseCase: AddBookUseCase,
    private val updateBookUseCase: UpdateBookUseCase
) {

    fun create(book: Book?): AddBookScreenStore =
        object : AddBookScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddBookScreenStore",
            initialState = State(bookForUpdate = book),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                is Msg.BookLoaded -> copy(loadedBook = msg.selectedBook)
            }
        }
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

                is Intent.LoadBookFromBase -> dispatch(BookLoaded(intent.selectedBook))
                is Intent.ClickUpdateBook -> {
                    scope.launch {
                        updateBookUseCase(intent.book)
                        publish(Label.ClickSaveBook)
                    }
                }
            }
        }
    }
}

private sealed interface Msg {

    data class BookLoaded(val selectedBook: BookItem) : Msg

}