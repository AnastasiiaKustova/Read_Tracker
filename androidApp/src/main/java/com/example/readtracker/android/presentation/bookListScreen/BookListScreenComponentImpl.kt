package com.example.readtracker.android.presentation.bookListScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenStore
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

class BookListScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: BookListScreenStoreFactory,
    @Assisted("collectionId") private val collectionId: String?,
    @Assisted("bookStatus") private val bookStatus: BookStatus?,
    @Assisted("openMode") openMode: BookListMode,
    @Assisted("onBackClicked") onBackClicked: () -> Unit,
    @Assisted("onBookClicked") onBookClicked: (String) -> Unit,
    @Assisted("onMultiSelectConfirmed") onMultiSelectConfirmed: (Set<String>) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : BookListScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(collectionId, bookStatus, openMode) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<BookListScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                BookListScreenStore.Label.ClickBack -> onBackClicked()
                                is BookListScreenStore.Label.ClickBook -> onBookClicked(label.bookId)
                                is BookListScreenStore.Label.ClickMultiSelectConfirm -> onMultiSelectConfirmed(label.bookIds)
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

    override fun onBackClick() {
        store.accept(BookListScreenStore.Intent.ClickBack)
    }

    override fun onBookClick(bookId: String) {
        store.accept(BookListScreenStore.Intent.ClickBook(bookId))
    }

    override fun onMultiSelectConfirm(ids: Set<String>){
        store.accept(BookListScreenStore.Intent.ClickMultiSelectConfirm(ids))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("collectionId") collectionId: String?,
            @Assisted("bookStatus") bookStatus: BookStatus?,
            @Assisted("openMode") openMode: BookListMode,
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onBookClicked") onBookClicked: (String) -> Unit,
            @Assisted("onMultiSelectConfirmed") onMultiSelectConfirmed: (Set<String>) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): BookListScreenComponentImpl
    }
}