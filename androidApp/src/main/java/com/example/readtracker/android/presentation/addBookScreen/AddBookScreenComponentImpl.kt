package com.example.readtracker.android.presentation.addBookScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AddBookScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: AddBookScreenStoreFactory,
    @Assisted("book") book: Book?,
    @Assisted("onSearchLitresClicked") private val onSearchLitresClicked: () -> Unit,
    @Assisted("onSaveBookClicked") private val onSaveBookClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : AddBookScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(book) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddBookScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                AddBookScreenStore.Label.ClickSearchLitres -> onSearchLitresClicked()
                                is AddBookScreenStore.Label.ClickSaveBook -> onSaveBookClicked()
                            }
                        }
                    }
                }
            }

            override fun onStop() {
                // Предотвращаем утечки памяти при переходе на экран деталей книги
                scope?.cancel()
                scope = null
            }
        })
    }

    override fun onSearchLitresClick() {
        store.accept(AddBookScreenStore.Intent.ClickSearchLitres)
    }

    override fun onSaveBookClick(addBookInput: AddBookInput) {
        store.accept(AddBookScreenStore.Intent.ClickSaveBook(addBookInput))
    }

    override fun onUpdateBookClick(book: Book) {
        store.accept(AddBookScreenStore.Intent.ClickUpdateBook(book))
    }

    override fun onSelectBookFromBaseClicked(selectedBook: BookItem) {
        store.accept(AddBookScreenStore.Intent.LoadBookFromBase(selectedBook = selectedBook))
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("book") book: Book?,
            @Assisted("onSearchLitresClicked") onSearchLitresClicked: () -> Unit,
            @Assisted("onSaveBookClicked") onSaveBookClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): AddBookScreenComponentImpl
    }
}