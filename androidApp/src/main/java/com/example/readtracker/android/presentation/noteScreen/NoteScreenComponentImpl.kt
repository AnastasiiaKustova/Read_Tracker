package com.example.readtracker.android.presentation.noteScreen

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

class NoteScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: NoteScreenStoreFactory,
    @Assisted("onNoteClicked") private val onNoteClicked: (String) -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : NoteScreenComponent, ComponentContext by componentContext {

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
                                is NoteScreenStore.Label.ClickNote -> onNoteClicked(label.noteId)
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

    override fun onNoteClick(noteId: String) {
        store.accept(NoteScreenStore.Intent.ClickNote(noteId))
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onNoteClicked") onNoteClicked: (String) -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): NoteScreenComponentImpl
    }
}