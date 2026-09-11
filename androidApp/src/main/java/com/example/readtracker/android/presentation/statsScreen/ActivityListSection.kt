package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.core.formatTimestamp
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.stats.ReadStat

@Composable
fun ActivityListSection(title: String, activities: List<ReadStat>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        activities.forEach { activity ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Иконка слева
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2C2C30)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activity.statusChangedTo == BookStatus.FINISHED) Icons.Default.CheckCircle else Icons.Default.Menu,
                            contentDescription = null,
                            tint = activity.statusChangedTo?.color ?: PrimaryCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 2. Основной контент (занимает всё свободное место)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Строка времени создания записи (теперь она никому не мешает)
                        Text(
                            text = activity.timestamp.formatTimestamp(),
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )

                        // Строка с прочитанными страницами и минутами
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${activity.pagesRead} стр. ",
                                color = Color(0xFFFFB300),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "(${activity.durationMinutes} мин)",
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // Название книги
                        Text(
                            text = activity.book.title,
                            color = TextPrimary, // Сделаем название книги поярче
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Плашка статуса (если он изменился)
                        activity.statusChangedTo?.let { status ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = status.color.copy(alpha = 0.15f),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Статус: ${status.title}",
                                    color = status.color,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}