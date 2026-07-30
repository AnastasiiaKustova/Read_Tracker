package com.example.readtracker.android.presentation.mainScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.chromium.base.Callback
import org.chromium.base.Log

class MainScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: MainScreenStoreFactory,
    @Assisted("onBookClicked") private val onBookClicked: (String) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : MainScreenComponent, ComponentContext by componentContext {

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
                                is MainScreenStore.Label.ClickBook -> {
                                    Log.d("APP_DEBUG", "3. COMPONENT: Перехвачен Label из MVI стора (bookId = ${label.bookId}). Вызываем навигацию.")
                                    onBookClicked(label.bookId)
                                }
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

    override fun onBookClick(bookId: String) {
        Log.d("APP_DEBUG", "2. COMPONENT: Метод onBookClicked($bookId) вызван. Отправляем Intent ClickBook в MVI стор.")
        store.accept(MainScreenStore.Intent.ClickBook(bookId))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onBookClicked") onBookClicked: (String) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): MainScreenComponentImpl
    }
}