package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AutoscaledBarChart(
    heights: List<Float>,
    labels: List<String>,
    selectedIndex: Int,
    onBarClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // РЯД 1: ТОЛЬКО СТОЛБИКИ ГРАФИКА (Выровнены строго по нижнему краю)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp), // Фиксированная высота для области графиков
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            heights.forEachIndexed { index, weight ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(weight.coerceIn(0.05f, 1f)) // Задаем высоту от доступного контейнера
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(
                            if (isSelected) PrimaryCyan else PrimaryCyan.copy(alpha = 0.2f) // Остальные блекнут, выбранный — яркий
                        )
                        .clickable { onBarClick(index) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // РЯД 2: ТОЛЬКО ТЕКСТОВЫЕ ПОДПИСИ (Они полностью независимы от высоты графиков)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            labels.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex

                Text(
                    text = label,
                    modifier = Modifier.weight(1f), // Вес совпадает, поэтому текст встанет строго под своим баром
                    color = if (isSelected) Color.White else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}