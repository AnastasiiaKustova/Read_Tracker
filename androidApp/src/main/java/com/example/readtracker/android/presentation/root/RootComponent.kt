package com.example.readtracker.android.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.BookDetailMode
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.BookListMode
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.BottomTab
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenComponent
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenComponent
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenComponent
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenComponent
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenComponent
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponent
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenComponent
import com.example.readtracker.android.presentation.noteScreen.NoteScreenComponent
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenComponent
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenComponent
import com.example.readtracker.android.presentation.statsScreen.StatsScreenComponent
import kotlinx.parcelize.Parcelize

interface RootComponent {

    val stack : Value<ChildStack<*, Child>>

    fun onTabSelected(tab: BottomTab)

    fun onAddBookClicked(book: Book? = null)

    fun onAddNoteClicked()

    fun onAddCollectionClicked()

    fun onBookClicked(bookId: String)

    fun onBookClicked(bookItem: BookItem, onChooseClicked: (BookItem) -> Unit)

    fun onNoteClicked(bookId: String)

    fun onSearchLitresClicked(onChooseClicked: (BookItem) -> Unit)

    fun openBookListByCollectionIdClick(collectionId: String, openMode: BookListMode)

    fun openBookListByBookStatusClick(bookStatus: BookStatus, openMode: BookListMode)

    fun openBookListClick(openMode: BookListMode, onResult: ((Set<String>) -> Unit)? = null)

    sealed class Configuration : Parcelable {
        @Parcelize
        data object MainScreen : Configuration()
        @Parcelize data object Notes : Configuration()
        @Parcelize data object Stats : Configuration()
        @Parcelize data object Profile : Configuration()
        @Parcelize data class BookDetail(
            val mode: BookDetailMode,
            val bookId: String? = null,
            val bookItem: BookItem? = null,
            @Transient val onChooseClicked: ((BookItem) -> Unit)? = null
        ) : Configuration()
        @Parcelize data class NoteDetail(val noteId: String) : Configuration()
        @Parcelize data class BookList(
            val collectionId: String?,
            val bookStatus: BookStatus?,
            val openMode: BookListMode,
            @Transient val onResult: ((Set<String>) -> Unit)? = null
        ) : Configuration()
        @Parcelize data class AddBook(val book: Book? = null) : Configuration()
        @Parcelize data object AddNote : Configuration()
        @Parcelize data object AddCollection : Configuration()
        @Parcelize data class SearchBook(
            @Transient val onBookSelected: (BookItem) -> Unit
        ) : Configuration()
    }

    sealed interface Child{
        class MainScreen(val component: MainScreenComponent): Child
        class NoteScreen(val component: NoteScreenComponent): Child
        class StatsScreen(val component: StatsScreenComponent) : Child
        class ProfileScreen(val component: ProfileScreenComponent) : Child
        class BookDetail(val component: BookDetailScreenComponent) : Child
        class NoteDetail(val component: NoteDetailScreenComponent) : Child
        class BookList(val component: BookListScreenComponent) : Child
        class AddBook(val component: AddBookScreenComponent) : Child
        class AddNote(val component: AddNoteScreenComponent) : Child
        class AddCollection(val component: AddCollectionScreenComponent) : Child
        class SearchBook(val component: SearchBookScreenComponent) : Child
    }
}