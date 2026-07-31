package com.example.readtracker.android.presentation.noteDetailScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.example.readtracker.android.domain.entity.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NoteDetailScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: NoteDetailScreenStoreFactory,
    @Assisted("onEditClicked") private val onEditClicked: () -> Unit,
    @Assisted("onDeleteClicked") private val onDeleteClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
    @Assisted("note") private val note: Note,
) : NoteDetailScreenComponent, ComponentContext by componentContext {

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
                                NoteDetailScreenStore.Label.ClickEdit -> onEditClicked()
                                NoteDetailScreenStore.Label.ClickDelete -> onDeleteClicked()
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

    override fun onEditClick() {
        store.accept(NoteDetailScreenStore.Intent.ClickEdit)
    }

    override fun onDeleteClick() {
        store.accept(NoteDetailScreenStore.Intent.ClickDelete)
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("note") note: Note,
            @Assisted("onEditClicked") onEditClicked: () -> Unit,
            @Assisted("onDeleteClicked") onDeleteClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): NoteDetailScreenComponentImpl
    }
}