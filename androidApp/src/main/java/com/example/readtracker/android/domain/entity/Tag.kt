package com.example.readtracker.android.domain.entity

data class Tag (
    val id: String,
    val title: String,
    //val frequency: Float
){
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