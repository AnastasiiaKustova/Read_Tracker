package com.example.readtracker.android.domain.entity.tag

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag (
    val id: String,
    val title: String,
    //val frequency: Float
) : Parcelable {
    companion object{
        fun test1() = Tag(
            "01",
            "Интересное",
        )
        fun test2() = Tag(
            "02",
            "Грустное",
        )
    }
}