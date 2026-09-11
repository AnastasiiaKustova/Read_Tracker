package com.example.readtracker.android.domain.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BookStatus(val title: String, val icon: ImageVector, val color: Color) {
    READING("Читаю", Icons.Default.PlayArrow, Color(0xFF26A6D1)),
    FINISHED("Прочитано", Icons.Default.Check, Color(0xFF388E3C)) ,
    ON_HOLD("На паузе", Icons.Default.Refresh, Color(0xFFFFA000)),
    DROPPED("Брошено", Icons.Default.Warning, Color(0xFFD32F2F))
}
