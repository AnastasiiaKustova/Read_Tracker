package com.example.readtracker.android.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class MainScreenItem (
    val books: Set<Book>,
    val collections: Set<BookCollection>
){
    companion object{
        fun default() = MainScreenItem(emptySet(), emptySet())
    }
}