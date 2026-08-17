package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.BookDetailMode
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookStatus
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

class BookDetailScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: BookDetailScreenStoreFactory,
    @Assisted("onEditBookClicked") private val onEditBookClicked: (book: Book) -> Unit,
    @Assisted("onChooseClicked") private val onChooseClicked: (bookItem: BookItem) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
    @Assisted("bookId") private val bookId: String?,
    @Assisted("bookItem") private val bookItem: BookItem?,
    @Assisted("mode") private val mode: BookDetailMode,
) : BookDetailScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(bookId, bookItem, mode) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<BookDetailScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                is BookDetailScreenStore.Label.ClickEditBook -> onEditBookClicked(label.book)
                                is BookDetailScreenStore.Label.ClickChoose -> onChooseClicked(label.bookItem)
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



    override fun onEditBookClick(book: Book) {
        store.accept(BookDetailScreenStore.Intent.ClickEditBook(book))
    }

    override fun onChangeStatusClick(newStatus: BookStatus) {
        store.accept(BookDetailScreenStore.Intent.ClickChangeStatus(newStatus))
    }

    override fun onUpdatePageClick(newPage: Int) {
        store.accept(BookDetailScreenStore.Intent.ClickUpdatePage(newPage))
    }

    override fun onChooseClick(bookItem: BookItem) {
        store.accept(BookDetailScreenStore.Intent.ClickChoose(bookItem))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("bookId") bookId: String?,
            @Assisted("bookItem") bookItem: BookItem?,
            @Assisted("mode") mode: BookDetailMode,
            @Assisted("onEditBookClicked") onEditBookClicked: (book: Book) -> Unit,
            @Assisted("onChooseClicked") onChooseClicked: (bookItem: BookItem) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): BookDetailScreenComponentImpl
    }
}