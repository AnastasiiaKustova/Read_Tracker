package com.example.readtracker.android.presentation.root

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.BottomTab
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenComponentImpl
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenComponentImpl
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenComponentImpl
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponentImpl
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenComponentImpl
import com.example.readtracker.android.presentation.noteScreen.NoteScreenComponentImpl
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenComponentImpl
import com.example.readtracker.android.presentation.root.RootComponent.Child.*
import com.example.readtracker.android.presentation.statsScreen.StatsScreenComponentImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.chromium.base.Log


class RootComponentImpl @AssistedInject constructor(
    private val mainScreenComponentImplFactory: MainScreenComponentImpl.Factory,
    private val noteScreenComponentImplFactory: NoteScreenComponentImpl.Factory,
    private val statsScreenComponentImplFactory: StatsScreenComponentImpl.Factory,
    private val profileScreenComponentImplFactory: ProfileScreenComponentImpl.Factory,
    private val bookDetailScreenComponentImplFactory: BookDetailScreenComponentImpl.Factory,
    private val noteDetailScreenComponentImplFactory: NoteDetailScreenComponentImpl.Factory,
    private val bookListScreenComponentImplFactory: BookListScreenComponentImpl.Factory,
    private val addBookScreenComponentImplFactory: AddBookScreenComponentImpl.Factory,
    @Assisted("onExitApp") private val onExitApp: () -> Unit,
    @Assisted("componentContext") componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    // 1. ИСПРАВЛЕНИЕ: Используем RootComponent.Configuration вместо старого Config
    private val navigation = StackNavigation<RootComponent.Configuration>()

    // 2. ИСПРАВЛЕНИЕ: Указываем правильную стартовую конфигурацию MainScreen
    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = RootComponent.Configuration.MainScreen,
        handleBackButton = true,
        childFactory = ::child
    )

    // 3. ИСПРАВЛЕНИЕ: Переключаем вкладки через публичный RootComponent.Configuration
    override fun onTabSelected(tab: BottomTab) {
        navigation.popTo(index = 0)
        val config = when (tab) {
            BottomTab.MAIN -> RootComponent.Configuration.MainScreen
            BottomTab.NOTES -> RootComponent.Configuration.Notes
            BottomTab.STATS -> RootComponent.Configuration.Stats
            BottomTab.PROFILE -> RootComponent.Configuration.Profile
        }
        navigation.bringToFront(config)
    }

    // 4. ИСПРАВЛЕНИЕ: Пушим правильный объект навигации деталей книги
    override fun onBookClicked(bookId: String) {
        Log.d("APP_DEBUG", "4. ROOT_NAV: Метод onBookClicked($bookId) выполнен. Делаем navigation.push().")
        navigation.push(RootComponent.Configuration.BookDetail(bookId))
    }

    override fun onAddBookClicked() {
        navigation.push(RootComponent.Configuration.AddBook)
    }

    override fun onNoteClicked(noteId: String) {
        navigation.push(RootComponent.Configuration.NoteDetail(noteId))
    }

    override fun onCollectionClick(collectionId: String){
        navigation.push(RootComponent.Configuration.BookList(collectionId, null))
    }

    override fun onCollectionClick(bookStatus: BookStatus){
        navigation.push(RootComponent.Configuration.BookList(null, bookStatus))
    }

    // 5. ИСПРАВЛЕНИЕ: Фабрика создания экранов теперь принимает RootComponent.Configuration
    private fun child(
        config: RootComponent.Configuration,
        componentContext: ComponentContext
    ): RootComponent.Child {
        Log.d("APP_DEBUG", "5. ROOT_NAV: Фабрика child создает экран для конфигурации: $config")
        return when (config) {
            RootComponent.Configuration.MainScreen -> {
                val component = mainScreenComponentImplFactory.create(
                    onAddBookClicked = {
                        onAddBookClicked() },
                    onBookClicked = { bookId ->
                        Log.d("APP_DEBUG", "6. ROOT_NAV: Успешно создаем Child.BookDetail для ID = ${bookId}")
                        onBookClicked(bookId) },
                    onBookStatusClicked = { collectionId ->
                        onCollectionClick(collectionId)
                    },
                    onCollectionClicked = { bookStatus ->
                        onCollectionClick(bookStatus)
                    },
                    componentContext = componentContext
                )
                MainScreen(component)
            }
            RootComponent.Configuration.Notes -> {
                val component = noteScreenComponentImplFactory.create(
                    onNoteClicked = { noteId ->
                        onNoteClicked(noteId) },
                    componentContext = componentContext
                )
                NoteScreen(component)
            }
            RootComponent.Configuration.Stats -> {
                val component = statsScreenComponentImplFactory.create(
                    componentContext = componentContext
                )
                StatsScreen(component)
            }
            RootComponent.Configuration.Profile -> {
                val component = profileScreenComponentImplFactory.create(
                    componentContext = componentContext
                )
                ProfileScreen(component)
            }
            // Проверка через is, так как BookDetail — это data class с параметром bookId
            is RootComponent.Configuration.BookDetail -> {
                val component = bookDetailScreenComponentImplFactory.create(
                    bookId = config.bookId,
                    onEditBookClicked = {},
                    onUpdatePageClicked = {},
                    onChangeStatusClicked = {},
                    componentContext = componentContext
                )
                BookDetail(component)
            }

            is RootComponent.Configuration.NoteDetail -> {
                val component = noteDetailScreenComponentImplFactory.create(
                    noteId = config.noteId,
                    onEditClicked = {},
                    onDeleteClicked = {},
                    componentContext = componentContext
                )
                NoteDetail(component)
            }

            is RootComponent.Configuration.BookList -> {

                val component = bookListScreenComponentImplFactory.create(
                    collectionId = config.collectionId,
                    bookStatus = config.bookStatus,
                    onBackClicked = {
                        navigation.pop()
                    },
                    onBookClicked = { bookId ->
                        onBookClicked(bookId)
                    },
                    componentContext = componentContext
                )
                BookList(component)
            }

            RootComponent.Configuration.AddBook -> {

                val component = addBookScreenComponentImplFactory.create(
                    onSearchLitresClicked = {},
                    onSaveBookClicked = { navigation.pop()},
                    componentContext = componentContext
                )
                AddBook(component)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("onExitApp") onExitApp: () -> Unit,
            @Assisted("componentContext") componentContext: ComponentContext,
        ): RootComponentImpl
    }
}