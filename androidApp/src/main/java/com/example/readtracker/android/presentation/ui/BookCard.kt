package com.example.readtracker.android.presentation.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.readtracker.android.core.formatWithSpace
import java.io.File
import java.net.URI
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(
    id: String?,
    title: String,
    author: String,
    currentPage: Int,
    totalPages: Int,
    coverUri: Uri?,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalPages > 0) currentPage.toFloat() / totalPages else 0f
    val percentage = round((progress * 100)).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Card(
            onClick = onCardClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE5E5E5)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(110.dp))
                    Spacer(modifier = Modifier.width(16.dp))

                    // Основная колонка для текстов занимает всю высоту
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(bottom = 20.dp) // Фиксированный отступ до линии прогресса
                    ) {
                        // --- ВЕРХНЯЯ ЧАСТЬ (Растет сверху вниз) ---
                        Text(
                            text = "${currentPage.formatWithSpace()} / ${totalPages.formatWithSpace()} страниц",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 3, // Занимает до 3 строк при необходимости
                            overflow = TextOverflow.Ellipsis
                        )

                        // --- ПРУЖИНА ---
                        // Этот Spacer выталкивает автора вниз, заполняя пустоту,
                        // если название короткое (например, в одну строку)
                        Spacer(modifier = Modifier.weight(1f))

                        // --- НИЖНЯЯ ЧАСТЬ (Всегда на одном месте) ---
                        Text(
                            text = author,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Индикатор прогресса на самом дне
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF007AFF),
                        trackColor = Color(0xFFCCCCCC)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }

        val lastModified = remember(coverUri) {
            try {
                // Если это локальный файл приложения, берем время его изменения
                if (coverUri != null && coverUri.scheme == "file") {
                    File(URI(coverUri.toString())).lastModified().toString()
                } else {
                    System.currentTimeMillis().toString() // Для сети генерируем свежий ключ
                }
            } catch (e: Exception) {
                ""
            }
        }

        // Обложка
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .width(110.dp)
                .height(164.dp)
                .align(Alignment.TopStart)
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFB0B3B8))
        ){
            if (coverUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUri)
                        .memoryCacheKey("${id}_$lastModified")
                        .diskCacheKey("${id}_$lastModified")
                        .build(),
                    contentDescription = "Обложка книги",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview
@Composable
fun BookCardTest(){
    BookCard(
        "",
        "Очень длинное название",
        "Автор Такойто",
        12345,
        15456, null, {}
    )
}