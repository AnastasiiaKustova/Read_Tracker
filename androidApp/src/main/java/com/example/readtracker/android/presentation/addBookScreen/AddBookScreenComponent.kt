package com.example.readtracker.android.presentation.addBookScreen

import android.net.Uri

interface AddBookScreenComponent {
    fun onSearchLitresClick()
    fun onSaveBookClick(title: String, author: String, pages: Int, desc: String, coverUri: Uri?)
}