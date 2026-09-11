package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookRatingBlock(
    rating: Int?,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(16.dp),
        // УБРАЛИ padding отсюда, чтобы плашка встала в один ряд по ширине с прогресс-баром!
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp), // Аккуратный внутренний отступ
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Левая часть: Подпись + Пробел + Цифра
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp) // Вот этот пробел перед цифрой 5!
            ) {
                Text(
                    text = "Ваша оценка книги",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                if (rating != null && rating > 0) {
                    Text(
                        text = rating.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Правая часть: Только звёзды
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp), // Расстояние между звездами
                verticalAlignment = Alignment.CenterVertically
            ) {
                val finalRating = rating ?: 0
                for (i in 1..5) {
                    val isFilled = i <= finalRating

                    // ИСПРАВЛЕНО: Используем только Filled иконку, но меняем ей прозрачность (alpha),
                    // чтобы пятая звезда не сжималась в точку и выглядела идеально по размеру!
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (isFilled) Color(0xFFFFD700) else Color.Black.copy(alpha = 0.15f),
                        modifier = Modifier.size(18.dp) // Оптимальный аккуратный размер
                    )
                }
            }
        }
    }
}