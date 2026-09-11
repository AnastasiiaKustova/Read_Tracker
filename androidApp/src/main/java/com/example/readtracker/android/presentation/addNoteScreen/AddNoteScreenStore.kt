package com.example.readtracker.android.presentation.addNoteScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.useCases.AddNoteUseCase
import com.example.readtracker.android.domain.useCases.AddTagUseCase
import com.example.readtracker.android.domain.useCases.GetBookByIdUseCase
import com.example.readtracker.android.domain.useCases.GetTagsUseCase
import com.example.readtracker.android.domain.useCases.ObserveNoteScreenDataUseCase
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.Intent
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.Label
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.State
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStore.State.ScreenState.*
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenStoreFactory.Msg.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AddNoteScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ClickSave(val addNoteInput: AddNoteInput) : Intent
        data class ClickAddTag(val addTagInput: AddTagInput) : Intent
        data class UpdateSelectedBook(val id: String) : Intent
        data object ClickSelectBook : Intent
    }

    data class State(
        val screenState: ScreenState
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data object Loading : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val selectedBook: Book?,
                val allAvailableTags: Set<Tag>
            ) : ScreenState
        }
    }

    sealed interface Label {
        data object ClickSave : Label
        data object ClickSelectBook : Label
    }
}

class AddNoteScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val addNoteUseCase: AddNoteUseCase,
    private val addTagUseCase: AddTagUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val observeNoteScreenDataUseCase: ObserveNoteScreenDataUseCase
) {

    fun create(): AddNoteScreenStore =
        object : AddNoteScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddNoteScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val selectedBook: Book?, val allAvailableTags: Set<Tag>) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action

        data class TagsInternalUpdated(val tags: Set<Tag>) : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(val selectedBook: Book?, val allAvailableTags: Set<Tag>) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg

        data class BookUpdated(val selectedBook: Book?) : Msg

        data class BookTagsUpdated(val allAvailableTags: Set<Tag>) : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
            }

            scope.launch {
                try {
                    var isFirstLoad = true // Флаг для отслеживания первой загрузки

                    withContext(Dispatchers.IO) {
                        observeNoteScreenDataUseCase()
                            .distinctUntilChanged()
                            .collect { noteScreenItem ->
                                withContext(Dispatchers.Main) {
                                    if (isFirstLoad) {
                                        // 1. При самом первом получении данных переводим экран в состояние Loaded
                                        dispatch(
                                            Action.ScreenLoaded(
                                                selectedBook = null, // Изначально книга не выбрана
                                                allAvailableTags = noteScreenItem.tags
                                            )
                                        )
                                        isFirstLoad = false // Сбрасываем флаг
                                    } else {
                                        // 2. Все последующие разы (например, при создании нового тега) обновляем ТОЛЬКО теги
                                        dispatch(Action.TagsInternalUpdated(noteScreenItem.tags))
                                    }
                                }
                            }
                    }
                } catch (e: Exception) {
                    dispatch(Action.ScreenError)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                ScreenError -> copy(screenState = Error)
                ScreenLoading -> copy(screenState = Loading)
                is ScreenLoaded -> copy(
                    screenState = Loaded(
                        msg.selectedBook,
                        msg.allAvailableTags
                    )
                )

                is BookUpdated -> {
                    val current = screenState as? Loaded ?: return this
                    copy(screenState = current.copy(selectedBook = msg.selectedBook))
                }

                is BookTagsUpdated -> {
                    val current = screenState as? State.ScreenState.Loaded ?: return this
                    copy(screenState = current.copy(allAvailableTags = msg.allAvailableTags))
                }
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> dispatch(
                    ScreenLoaded(action.selectedBook, action.allAvailableTags)
                )

                Action.ScreenError -> dispatch(
                    ScreenError
                )

                Action.ScreenLoading -> dispatch(
                    ScreenLoading
                )

                is Action.TagsInternalUpdated -> dispatch(Msg.BookTagsUpdated(action.tags))
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ClickSave -> {
                    scope.launch {
                        addNoteUseCase(intent.addNoteInput)
                        publish(Label.ClickSave)
                    }
                }

                Intent.ClickSelectBook -> publish(Label.ClickSelectBook)
                is Intent.UpdateSelectedBook -> {
                    scope.launch {
                        val selectedBook = getBookByIdUseCase(intent.id)
                        dispatch(Msg.BookUpdated(selectedBook))
                    }
                }

                is Intent.ClickAddTag ->
                    scope.launch {
                        addTagUseCase(intent.addTagInput)
                    }
            }
        }
    }
}