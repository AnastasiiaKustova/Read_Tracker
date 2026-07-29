package com.example.readtracker.android.domain.entity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

enum class BookStatus(val title: String, val icon: ImageVector) {
    READING("Читаю", Icons.Default.PlayArrow),
    FINISHED("Прочитано", Icons.Default.Check),
    ON_HOLD("На паузе", Icons.Default.Refresh),
    DROPPED("Брошено", Icons.Default.Warning)
}
