package com.example.readtracker.android.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainScreenItem (
    val test: String = "",
): Parcelable{
    companion object{
        fun default() = MainScreenItem()
    }
}