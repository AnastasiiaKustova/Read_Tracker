package com.example.readtracker.android.presentation.addCollectionScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.AddCollectionInput
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

class AddCollectionScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: AddCollectionScreenStoreFactory,
    @Assisted("onSaveClicked") private val onSaveClicked: () -> Unit,
    @Assisted("onAddBooksClicked") private val onAddBooksClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : AddCollectionScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddCollectionScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                is AddCollectionScreenStore.Label.ClickSave -> onSaveClicked()
                                is AddCollectionScreenStore.Label.ClickAddBooks -> onAddBooksClicked()
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

    override fun onSaveClick(addCollectionInput: AddCollectionInput) {
        store.accept(AddCollectionScreenStore.Intent.ClickSave(addCollectionInput))
    }

    override fun onRemoveBookClick(id: String) {
        store.accept(AddCollectionScreenStore.Intent.ClickRemoveBook(id))
    }

    override fun onAddBooks() {
        store.accept(AddCollectionScreenStore.Intent.ClickAddBooks)
    }

    override fun onBooksSelected(ids: Set<String>) {
        // Вот теперь это абсолютно законно: компонент имеет прямой доступ к своему стору!
        store.accept(AddCollectionScreenStore.Intent.UpdateSelectedBooks(ids))
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onSaveClicked") onSaveClicked: () -> Unit,
            @Assisted("onAddBooksClicked") onAddBooksClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): AddCollectionScreenComponentImpl
    }
}