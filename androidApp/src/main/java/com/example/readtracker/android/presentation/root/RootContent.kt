package com.example.readtracker.android.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.example.readtracker.android.presentation.mainScreen.MainScreenContent
import com.example.readtracker.android.presentation.ui.AppTheme

@Composable
fun RootContent(component: RootComponent) {
    AppTheme{
        Box(modifier = Modifier.fillMaxSize()){
            Children(stack = component.stack) {
                when (val instance = it.instance){
                    is RootComponent.Child.MainScreen -> MainScreenContent(component = instance.component)
                }
            }
        }
    }
}