package com.example.readtracker.android.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class BookItem(
    // Основные текстовые и числовые поля книги
    @SerialName("_id") val id: Long,
    @SerialName("name") val title: String? = "Без названия",
    @SerialName("author") val author: String? = "Автор не указан",
    @SerialName("description") val description: String? = "Описание отсутствует",
    @SerialName("picture") val picture: String? = null,
    @SerialName("url") val url: String? = "",
    @SerialName("price") val price: Double? = 0.0,
    @SerialName("oldprice") val oldPrice: String? = null,
    @SerialName("currencyId") val currencyId: String? = "RUR",
    @SerialName("categoryId") val categoryId: Long? = null,
    @SerialName("publisher") val publisher: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("ISBN") val isbn: String? = null,
    @SerialName("litres_isbn") val litresIsbn: String? = null,
    @SerialName("genres_list") val genresList: String? = null,
    @SerialName("age") val age: Int? = 0,
    @SerialName("lang") val lang: String? = "ru",
    @SerialName("series") val series: String? = null,
    @SerialName("vendor") val vendor: String? = null,

    // Булевые флаги (Логические да/нет)
    @SerialName("_available") val available: Boolean? = true,
    @SerialName("downloadable") val downloadable: Boolean? = true,
    @SerialName("abonement") val abonement: Boolean? = false,
    @SerialName("is_adult") val isAdult: Boolean? = false,
    @SerialName("enable_auto_discounts") val enableAutoDiscounts: Boolean? = true,
    @SerialName("manufacturer_warranty") val manufacturerWarranty: Boolean? = false,
    @SerialName("store") val store: Boolean? = false,
    @SerialName("pickup") val pickup: Boolean? = false,
    @SerialName("delivery") val delivery: Boolean? = false,

    // Сложные поля со спецсимволами (косая черта) из базы данных
    @SerialName("param/_name") val paramName: String? = null,
    @SerialName("param/__text") val paramText: String? = null
): Parcelable
