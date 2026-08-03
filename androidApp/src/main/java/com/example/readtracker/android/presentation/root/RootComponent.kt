package com.example.readtracker.android.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.BottomTab
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenComponent
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenComponent
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponent
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenComponent
import com.example.readtracker.android.presentation.noteScreen.NoteScreenComponent
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenComponent
import com.example.readtracker.android.presentation.statsScreen.StatsScreenComponent
import kotlinx.parcelize.Parcelize

interface RootComponent {

    val stack : Value<ChildStack<*, Child>>

    fun onTabSelected(tab: BottomTab)

    fun onBookClicked(bookId: String)

    fun onNoteClicked(bookId: String)

    fun onCollectionClick(collectionId: String)

    fun onCollectionClick(bookStatus: BookStatus)

    sealed class Configuration : Parcelable {
        @Parcelize
        data object MainScreen : Configuration()
        @Parcelize data object Notes : Configuration()
        @Parcelize data object Stats : Configuration()
        @Parcelize data object Profile : Configuration()
        @Parcelize data class BookDetail(val bookId: String) : Configuration()
        @Parcelize data class NoteDetail(val noteId: String) : Configuration()
        @Parcelize data class BookList(val collectionId: String?, val bookStatus: BookStatus?) : Configuration()
    }

    sealed interface Child{
        class MainScreen(val component: MainScreenComponent): Child
        class NoteScreen(val component: NoteScreenComponent): Child
        class StatsScreen(val component: StatsScreenComponent) : Child
        class ProfileScreen(val component: ProfileScreenComponent) : Child
        class BookDetail(val component: BookDetailScreenComponent) : Child
        class NoteDetail(val component: NoteDetailScreenComponent) : Child
        class BookList(val component: BookListScreenComponent) : Child
    }
}