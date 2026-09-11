package com.example.readtracker.android.presentation.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.popWhile
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.BookDetailMode
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.BottomTab
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenComponentImpl
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenComponentImpl
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenComponentImpl
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenComponentImpl
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenComponentImpl
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponentImpl
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenComponentImpl
import com.example.readtracker.android.presentation.noteScreen.NoteScreenComponentImpl
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenComponentImpl
import com.example.readtracker.android.presentation.root.RootComponent.Child.AddBook
import com.example.readtracker.android.presentation.root.RootComponent.Child.AddCollection
import com.example.readtracker.android.presentation.root.RootComponent.Child.AddNote
import com.example.readtracker.android.presentation.root.RootComponent.Child.BookDetail
import com.example.readtracker.android.presentation.root.RootComponent.Child.BookList
import com.example.readtracker.android.presentation.root.RootComponent.Child.MainScreen
import com.example.readtracker.android.presentation.root.RootComponent.Child.NoteDetail
import com.example.readtracker.android.presentation.root.RootComponent.Child.NoteScreen
import com.example.readtracker.android.presentation.root.RootComponent.Child.ProfileScreen
import com.example.readtracker.android.presentation.root.RootComponent.Child.StatsScreen
import com.example.readtracker.android.presentation.root.RootComponent.Child.SearchBook
import com.example.readtracker.android.presentation.root.RootComponent.Child.ReadTrackerScreen
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenComponentImpl
import com.example.readtracker.android.presentation.statsScreen.StatsScreenComponentImpl
import com.example.readtracker.android.presentation.trackerScreen.ReadTrackerScreenComponentImpl
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
    private val addNoteScreenComponentImplFactory: AddNoteScreenComponentImpl.Factory,
    private val addCollectionScreenComponentImplFactory: AddCollectionScreenComponentImpl.Factory,
    private val searchBookScreenComponentImplFactory: SearchBookScreenComponentImpl.Factory,
    private val readTrackerScreenComponentImplFactory: ReadTrackerScreenComponentImpl.Factory,
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
        Log.d(
            "APP_DEBUG",
            "4. ROOT_NAV: Метод onBookClicked($bookId) выполнен. Делаем navigation.push()."
        )
        navigation.push(
            RootComponent.Configuration.BookDetail(
                bookId = bookId,
                mode = BookDetailMode.VIEW
            )
        )
    }

    override fun onBookClicked(bookItem: BookItem, onChooseClicked: (BookItem) -> Unit) {
        navigation.push(
            RootComponent.Configuration.BookDetail(
                bookItem = bookItem,
                mode = BookDetailMode.SEARCH,
                onChooseClicked = onChooseClicked
            )
        )
    }

    override fun onAddBookClicked(book: Book?) {
        navigation.push(RootComponent.Configuration.AddBook(book))
    }

    override fun onAddNoteClicked() {
        navigation.push(RootComponent.Configuration.AddNote)
    }

    override fun onAddCollectionClicked() {
        navigation.push(RootComponent.Configuration.AddCollection)
    }

    override fun onNoteClicked(noteId: String) {
        navigation.push(RootComponent.Configuration.NoteDetail(noteId))
    }

    override fun onStartReadingClicked(bookId: String) {
        navigation.push(RootComponent.Configuration.ReadTracker(bookId))
    }

    override fun onSearchLitresClicked(onChooseClicked: (BookItem) -> Unit) {
        navigation.push(RootComponent.Configuration.SearchBook(onChooseClicked))
    }

    override fun openBookListByCollectionIdClick(collectionId: String, openMode: BookListMode) {
        navigation.push(RootComponent.Configuration.BookList(collectionId, null, openMode))
    }

    override fun openBookListByBookStatusClick(bookStatus: BookStatus, openMode: BookListMode) {
        navigation.push(RootComponent.Configuration.BookList(null, bookStatus, openMode))
    }

    override fun openBookListClick(openMode: BookListMode, onResult: ((Set<String>) -> Unit)?) {
        navigation.push(
            RootComponent.Configuration.BookList(
                null,
                null,
                openMode,
                onResult = onResult
            )
        )
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
                        onAddBookClicked()
                    },
                    onBookClicked = { bookId ->
                        Log.d(
                            "APP_DEBUG",
                            "6. ROOT_NAV: Успешно создаем Child.BookDetail для ID = ${bookId}"
                        )
                        onBookClicked(bookId)
                    },
                    openBookListByCollectionIdClicked = { collectionId ->
                        openBookListByCollectionIdClick(collectionId, BookListMode.VIEW)
                    },
                    openBookListByBookStatusClicked = { bookStatus ->
                        openBookListByBookStatusClick(bookStatus, BookListMode.VIEW)
                    },
                    onAddCollectionClicked = {
                        onAddCollectionClicked()
                    },
                    componentContext = componentContext
                )
                MainScreen(component)
            }

            RootComponent.Configuration.Notes -> {
                val component = noteScreenComponentImplFactory.create(
                    onAddNoteClicked = {
                        onAddNoteClicked()
                    },
                    onNoteClicked = { noteId ->
                        onNoteClicked(noteId)
                    },
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
                    bookItem = config.bookItem,
                    mode = config.mode,
                    onEditBookClicked = { book -> onAddBookClicked(book) },
                    onChooseClicked = { chosenBook ->
                        // 1. Стреляем результатом в самый первый экран!
                        config.onChooseClicked?.invoke(chosenBook)

                        // 2. Очищаем навигационный стек, закрывая Экран 3 и Экран 2 разом,
                        // чтобы пользователь мгновенно оказался на Экране 1 (Добавление книги)
                        navigation.popWhile { it is RootComponent.Configuration.SearchBook || it is RootComponent.Configuration.BookDetail }
                    },
                    onStartReadingClicked = { book -> onStartReadingClicked(book.id)},
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
                    openMode = config.openMode,
                    onBackClicked = {
                        navigation.pop()
                    },
                    onBookClicked = { bookId ->
                        if (config.openMode == BookListMode.SINGLE_SELECT) {
                            config.onResult?.invoke(setOf(bookId))
                            navigation.pop()
                        } else
                            onBookClicked(bookId)
                    },
                    onMultiSelectConfirmed = { selectedIds ->
                        config.onResult?.invoke(selectedIds)
                        navigation.pop()
                    },
                    componentContext = componentContext
                )
                BookList(component)
            }

            is RootComponent.Configuration.AddBook -> {
                var component: AddBook? = null
                val createdComponent = addBookScreenComponentImplFactory.create(
                    book = config.book,
                    onSearchLitresClicked = {
                        onSearchLitresClicked(
                            onChooseClicked = { chosenBook ->
                                (component?.component as? AddBookScreenComponentImpl)
                                    ?.onSelectBookFromBaseClicked(chosenBook)
                            })
                    },
                    onSaveBookClicked = { navigation.pop() },
                    componentContext = componentContext
                )
                AddBook(createdComponent).also {
                    component = it
                }
            }

            is RootComponent.Configuration.AddNote -> {
                var component: AddNote? = null
                val createdComponent = addNoteScreenComponentImplFactory.create(
                    onSaveClicked = { navigation.pop() },
                    onAddBookClicked = {
                        openBookListClick(
                            BookListMode.SINGLE_SELECT,
                            onResult = { selectedId ->
                                (component?.component as? AddNoteScreenComponentImpl)
                                    ?.onBookSelected(selectedId.first())
                            }
                        )

                    },
                    componentContext = componentContext
                )
                AddNote(createdComponent).also {
                    component = it
                }
            }

            RootComponent.Configuration.AddCollection -> {
                var component: AddCollection? = null
                val createdComponent = addCollectionScreenComponentImplFactory.create(
                    onSaveClicked = { navigation.pop() },
                    onAddBooksClicked = {
                        openBookListClick(
                            BookListMode.MULTI_SELECT,
                            onResult = { selectedIds ->
                                (component?.component as? AddCollectionScreenComponentImpl)
                                    ?.onBooksSelected(selectedIds)
                            }
                        )
                    },
                    componentContext = componentContext
                )
                AddCollection(createdComponent).also {
                    component = it
                }
            }

            is RootComponent.Configuration.SearchBook -> {
                val component = searchBookScreenComponentImplFactory.create(
                    onBookClicked = { bookItem -> onBookClicked(bookItem, config.onBookSelected) },
                    onBackClicked = { navigation.pop() },
                    componentContext = componentContext
                )
                SearchBook(component)
            }

            is RootComponent.Configuration.ReadTracker -> {
                val component = readTrackerScreenComponentImplFactory.create(
                    onBackClicked = { navigation.pop() },
                    bookId = config.bookId,
                    componentContext = componentContext
                )
                ReadTrackerScreen(component)
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