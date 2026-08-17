package com.example.readtracker.android.domain.entity.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CategoryItem (
    @SerialName("_id") val id: Long,
    @SerialName("__text") val title: String,
): Parcelable