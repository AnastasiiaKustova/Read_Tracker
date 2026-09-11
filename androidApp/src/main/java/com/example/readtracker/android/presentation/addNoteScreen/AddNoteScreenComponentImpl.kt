package com.example.readtracker.android.presentation.addNoteScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
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

class AddNoteScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: AddNoteScreenStoreFactory,
    @Assisted("onSaveClicked") private val onSaveClicked: () -> Unit,
    @Assisted("onAddBookClicked") private val onAddBookClicked: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext,
) : AddNoteScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<AddNoteScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {
                                is AddNoteScreenStore.Label.ClickSave -> onSaveClicked()
                                AddNoteScreenStore.Label.ClickSelectBook -> onAddBookClicked()
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

    override fun onSaveClick(addNoteInput: AddNoteInput) {
        store.accept(AddNoteScreenStore.Intent.ClickSave(addNoteInput))
    }

    override fun onAddTagClick(addTagInput: AddTagInput) {
        store.accept(AddNoteScreenStore.Intent.ClickAddTag(addTagInput))
    }

    override fun onBookSelected(bookId: String) {
        store.accept(AddNoteScreenStore.Intent.UpdateSelectedBook(bookId))
    }

    override fun onBookSelectClick() {
        store.accept(AddNoteScreenStore.Intent.ClickSelectBook)
    }


    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("onSaveClicked") onSaveClicked: () -> Unit,
            @Assisted("onAddBookClicked") onAddBookClicked: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): AddNoteScreenComponentImpl
    }
}