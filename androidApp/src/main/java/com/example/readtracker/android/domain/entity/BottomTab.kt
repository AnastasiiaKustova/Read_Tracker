package com.example.readtracker.android.domain.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomTab(val title: String, val icon: ImageVector) {
    MAIN("Главная", Icons.Default.Home),
    NOTES("Заметки", Icons.Default.List),
    STATS("Статистика", Icons.Default.Star),
    PROFILE("Профиль", Icons.Default.AccountCircle)
}