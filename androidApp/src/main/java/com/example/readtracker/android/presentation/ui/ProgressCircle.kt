package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressCircle(
    percentage: Int, // Принимает число от 0 до 100
    modifier: Modifier = Modifier
) {
    // Безопасно переводим Int (0-100) в Float (0.0-1.0) для прогресс-бара
    val coercedPercentage = percentage.coerceIn(0, 100)
    val progressFloat = coercedPercentage / 100f

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. ЗАДНИЙ ФОН КРУГА (темная подложка)
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.size(120.dp),
            color = Color(0xFF0A2B46),
            strokeWidth = 8.dp,
            trackColor = Color.Transparent
        )

        // 2. АКТИВНЫЙ ПРОГРЕСС (светлая линия)
        CircularProgressIndicator(
            progress = progressFloat,
            modifier = Modifier.size(120.dp),
            color = Color(0xFF2196F3),
            strokeWidth = 8.dp,
            strokeCap = StrokeCap.Round,
            trackColor = Color.Transparent
        )

        // 3. ТЕКСТ С ПРОЦЕНТАМИ ПО ЦЕНТРУ
        Text(
            text = "$coercedPercentage%",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
@Preview
fun TestProgressCircle(){
    ProgressCircle(30)
}