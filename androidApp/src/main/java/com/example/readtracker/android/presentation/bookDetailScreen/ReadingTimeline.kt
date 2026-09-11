package com.example.readtracker.android.presentation.bookDetailScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.core.formatTimestamp
import com.example.readtracker.android.core.getDaysBetween

@Composable
fun ReadingTimeline(
    firstReadingDate: Long,
    lastReadingDate: Long,
    finishedDate: Long?,
    modifier: Modifier = Modifier
) {
    // Определяем, какой режим показывать (Финиш или Последний раз)
    val isFinished = finishedDate != null
    val endDate = finishedDate ?: lastReadingDate

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- ЛЕВАЯ ЧАСТЬ: СТАРТ ---
        Column {
            Text(
                text = "Старт:",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = firstReadingDate.formatTimestamp("dd.MM.yyyy"),
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }

        // --- СРЕДНЯЯ ЧАСТЬ: СТРЕЛОЧКА И ДЛИТЕЛЬНОСТЬ ---
        if (firstReadingDate != null && endDate != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = getDaysBetween(firstReadingDate, endDate),
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Нативная тонкая стрелочка вправо
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- ПРАВАЯ ЧАСТЬ: ФИНИШ / ПОСЛЕДНИЙ РАЗ ---
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isFinished) "Финиш:" else "В последний раз:",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = endDate.formatTimestamp("dd.MM.yyyy"),
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}