package com.example.readtracker.android.presentation.addBookScreen

import com.example.readtracker.android.domain.entity.AddBookInput

interface AddBookScreenComponent {
    fun onSearchLitresClick()
    fun onSaveBookClick(addBookInput: AddBookInput)
}