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
        val DEFAULT_TAGS = setOf(
            Tag("def_1", "Важное"),
            Tag("def_2", "Инсайт"),
            Tag("def_3", "Цитата"),
            Tag("def_4", "Мотивация"),
            Tag("def_5", "Сюжет")
        )
    }
}