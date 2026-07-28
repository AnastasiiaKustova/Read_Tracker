package com.example.readtracker.android.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.example.readtracker.android.presentation.mainScreen.MainScreenComponent

interface RootComponent {

    val stack : Value<ChildStack<*, Child>>

    sealed interface Child{
        class MainScreen(val component: MainScreenComponent): Child
    }
}