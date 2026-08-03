package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionCard(
    title: String,
    bookCount: Int,
    onCollectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCollectionClick,
        modifier = modifier
            .width(160.dp)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5E5E5) // Серый фон карточки
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Название коллекции
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Нижняя часть: мини-обложки слева и счетчик справа
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Ограничиваем количество мини-обложек от 1 до 3
                val coversToShow = bookCount.coerceIn(1, 3)

                // Контейнер для стопки книг с наложением
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(70.dp) // Запас ширины под смещение мини-обложек
                ) {
                    for (i in 0 until coversToShow) {
                        Box(
                            modifier = Modifier
                                .offset(x = (i * 12).dp) // Каждая следующая сдвигается вправо
                                .width(28.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                // Делаем цвет каждой следующей чуть темнее для объема
                                .background(Color(0xFFB0B3B8 - (i * 0x101010)))
                        )
                    }
                }

                // Количество штук
                Text(
                    text = "$bookCount шт",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
fun CollectionCardTest(){
    CollectionCard(
        "Тест",
        5,
        {}
    )
}