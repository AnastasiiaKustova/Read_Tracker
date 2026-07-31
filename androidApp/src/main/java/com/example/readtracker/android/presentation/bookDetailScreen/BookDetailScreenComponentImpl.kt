package com.example.readtracker.android.presentation.bookDetailScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponentImpl
import com.example.readtracker.android.presentation.mainScreen.MainScreenStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BookDetailScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: BookDetailScreenStoreFactory,
    @Assisted("onEditBookClicked") private val onEditBookClicked: () -> Unit,
    @Assisted("onChangeStatusClicked") private val onChangeStatusClicked: () -> Unit,
    @Assisted("onUpdatePageClicked") private val onUpdatePageClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
    @Assisted("book") private val book: Book,
) : BookDetailScreenComponent, ComponentContext by componentContext {

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
                                BookDetailScreenStore.Label.ClickEditBook -> onEditBookClicked()
                                BookDetailScreenStore.Label.ClickChangeStatus -> onChangeStatusClicked()
                                BookDetailScreenStore.Label.ClickUpdatePage -> onUpdatePageClicked()
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



    override fun onEditBookClick() {
        store.accept(BookDetailScreenStore.Intent.ClickEditBook)
    }

    override fun onChangeStatusClick() {
        store.accept(BookDetailScreenStore.Intent.ClickChangeStatus)
    }

    override fun onUpdatePageClick() {
        store.accept(BookDetailScreenStore.Intent.ClickUpdatePage)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("book") book: Book,
            @Assisted("onEditBookClicked") onEditBookClicked: () -> Unit,
            @Assisted("onChangeStatusClicked") onChangeStatusClicked: () -> Unit,
            @Assisted("onUpdatePageClicked") onUpdatePageClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): BookDetailScreenComponentImpl
    }
}