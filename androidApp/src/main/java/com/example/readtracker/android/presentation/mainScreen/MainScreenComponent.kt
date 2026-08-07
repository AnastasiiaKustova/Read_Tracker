package com.example.readtracker.android.presentation.mainScreen

import com.example.readtracker.android.domain.entity.BookStatus
import kotlinx.coroutines.flow.StateFlow

interface MainScreenComponent {
    val model: StateFlow<MainScreenStore.State>
    fun onAddBookClick()
    fun onAddCollectionClick()
    fun onBookClick(bookId: String)
    fun onCollectionClick(collectionId: String)
    fun onCollectionClick(bookStatus: BookStatus)
}