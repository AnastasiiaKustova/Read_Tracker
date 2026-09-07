package com.example.readtracker.android.presentation.trackerScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
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

class ReadTrackerScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: ReadTrackerScreenStoreFactory,
    @Assisted("onBackClicked") onBackClicked: () -> Unit,
    @Assisted("bookId") private val bookId: String,
    @Assisted("componentContext") componentContext: ComponentContext,
) : ReadTrackerScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(bookId) }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<ReadTrackerScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                ReadTrackerScreenStore.Label.ClickBack -> onBackClicked()
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
        store.accept(ReadTrackerScreenStore.Intent.ClickBack)
    }

    override fun onFinishReading(newPage: Int, durationMinutes: Int) {
        store.accept(ReadTrackerScreenStore.Intent.ClickFinishRead(newPage, durationMinutes))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBackClicked") onBackClicked: () -> Unit,
            @Assisted("bookId") bookId: String,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): ReadTrackerScreenComponentImpl
    }
}