package com.example.readtracker.android.presentation.bookDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.core.formatWithSpace
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.presentation.ui.StatItem
import kotlin.math.round

@Composable
fun BookDetailScreen(
    book: Book,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Расчет прогресса для индикатора
    val progress = if (book.totalPages > 0) book.currentPage.toFloat() / book.totalPages else 0f
    val percentage = round((progress * 100)).toInt()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState) // Позволяет прокручивать длинное описание вниз
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Обложка книги по центру (пока серый прямоугольник)
        Box(
            modifier = Modifier
                .width(160.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFB0B3B8)) // Заглушка обложки
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Название книги
        Text(
            text = book.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Автор книги
        Text(
            text = book.author,
            fontSize = 16.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Панель статистики со скриншота (Страницы и Цитаты)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, // Равномерно распределяет элементы по ширине
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = book.totalPages.formatWithSpace(), label = "Страниц")
            StatItem(value = book.quotesCount.toString(), label = "Цитат")
            // Сюда в будущем можно легко дописать другие метрики ("Читают", "Полка")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Блок прогресса чтения
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Прогресс чтения",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "$percentage%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007AFF)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF007AFF),
                trackColor = Color(0xFFE5E5E5)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Прочитано ${book.currentPage.formatWithSpace()} из ${book.totalPages.formatWithSpace()} страниц",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Описание книги
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "О книге",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.description,
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
@Preview
fun BookDetailScreenTest(){
    BookDetailScreen(Book.test())
}