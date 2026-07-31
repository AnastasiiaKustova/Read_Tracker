package com.example.readtracker.android.presentation.bookListScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.readtracker.android.domain.entity.Book
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BookListScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: BookListScreenStoreFactory,
    @Assisted("onBackClicked") onBackClicked: () -> Unit,
    @Assisted("onBookClicked") onBookClicked: (String) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : BookListScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

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


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onBookClicked") onBookClicked: (String) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): BookListScreenComponentImpl
    }
}