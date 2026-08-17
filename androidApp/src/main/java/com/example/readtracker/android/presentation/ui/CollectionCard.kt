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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.readtracker.android.domain.entity.book.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionCard(
    title: String,
    bookCount: Int,
    books: Set<Book>,
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
                        // 1. Безопасно достаем книгу из списка по индексу i
                        val book = books.toList().getOrNull(i)

                        if (book != null) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (i * 12).dp) // Каждая следующая сдвигается вправо
                                    .width(28.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    // Вычисляем цвет фона. Для безопасности Котлина лучше использовать функцию Color.copy или фиксированные сдвиги
                                    .background(Color(0xFFB0B3B8 - (i * 0x101010))),
                                contentAlignment = Alignment.Center
                            ) {
                                // 2. ИСПРАВЛЕНИЕ: Обязательно проверяем наличие Uri
                                if (book.coverUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(book.coverUri)
                                            .memoryCacheKey(book.id) // Уникальный ключ по ID книги
                                            .diskCacheKey(book.id)
                                            .build(),
                                        contentDescription = "Обложка книги в стопке",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // 3. ИСПРАВЛЕНИЕ: Если обложки нет, оставляем Box пустым (он просто зальется цветом фона),
                                    // но принудительно заставляем Coil стереть старую картинку из этой переиспользованной ячейки!
                                    Spacer(modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
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
        emptySet(),
        {}
    )
}