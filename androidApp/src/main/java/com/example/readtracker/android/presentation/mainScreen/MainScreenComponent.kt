package com.example.readtracker.android.presentation.mainScreen

import com.example.readtracker.android.domain.entity.BookStatus

interface MainScreenComponent {
    fun onBookClick(bookId: String)
    fun onCollectionClick(collectionId: String)
    fun onCollectionClick(bookStatus: BookStatus)
}