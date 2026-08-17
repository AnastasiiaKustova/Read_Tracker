package com.example.readtracker.android.presentation.searchBookScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
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

class SearchBookScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: SearchBookScreenStoreFactory,
    @Assisted("onBackClicked") onBackClicked: () -> Unit,
    @Assisted("onBookClicked") onBookClicked: (BookItem) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : SearchBookScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<SearchBookScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                SearchBookScreenStore.Label.ClickBack -> onBackClicked()
                                is SearchBookScreenStore.Label.ClickBook -> onBookClicked(label.bookItem)
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
        store.accept(SearchBookScreenStore.Intent.ClickBack)
    }

    override fun onBookClick(bookItem: BookItem) {
        store.accept(SearchBookScreenStore.Intent.ClickBook(bookItem))
    }

    override fun onQueryChange(query: String) {
        store.accept(SearchBookScreenStore.Intent.ChangeQuery(query))
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("onBookClicked") onBookClicked: (BookItem) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): SearchBookScreenComponentImpl
    }
}