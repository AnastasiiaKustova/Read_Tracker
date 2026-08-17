package com.example.readtracker.android.presentation.addBookScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.readtracker.android.domain.entity.book.AddBookInput
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.presentation.common.CustomInputField

@Composable
fun AddBookScreenContent(component: AddBookScreenComponent) {
    val state by component.model.collectAsState()

    AddBookScreen(
        selectedBook = state.loadedBook,
        bookForUpdate = state.bookForUpdate,
        onSearchLitresClick = { component.onSearchLitresClick() },
        onSaveBookClick = { addBookInput: AddBookInput ->
            component.onSaveBookClick(addBookInput)
        },
        onUpdateBookClick = { book: Book ->
            component.onUpdateBookClick(book)
        },
    )
}

@Composable
fun AddBookScreen(
    selectedBook: BookItem?,
    bookForUpdate: Book?,
    onSearchLitresClick: () -> Unit,       // Переход на экран поиска ЛитРес
    onSaveBookClick: (addBookInput: AddBookInput) -> Unit,
    onUpdateBookClick: (book: Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isUpdate = bookForUpdate != null

    // Состояния для хранения введенных пользователем данных
    var titleText by remember {
        mutableStateOf(
            if (isUpdate) bookForUpdate.title else selectedBook?.title ?: ""
        )
    }
    var authorText by remember {
        mutableStateOf(
            if (isUpdate) bookForUpdate.author else selectedBook?.author ?: ""
        )
    }
    var seriesText by remember {
        mutableStateOf(
            if (isUpdate) bookForUpdate.series else selectedBook?.series ?: ""
        )
    }
    var pagesText by remember { mutableStateOf(if (isUpdate) bookForUpdate.totalPages.toString() else "") }
    var descriptionText by remember {
        mutableStateOf(
            if (isUpdate) bookForUpdate.description else selectedBook?.description ?: ""
        )
    }
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(if (isUpdate) bookForUpdate.coverUri else selectedBook?.picture?.let {
            Uri.parse(
                it
            )
        })
    }

    // Лаунчер для открытия системной галереи смартфона
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isUpdate) {
            // --- 1. КНОПКА ПОИСКА В ЛИТРЕС ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF007AFF)) // Акцентный синий цвет
                    .clickable { onSearchLitresClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Найти в ЛитРес",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // --- 2. ЗАГРУЗКА ОБЛОЖКИ ИЗ ГАЛЕРЕИ ---
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(190.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE5E5E5)) // Фирменный серый фон, если картинки нет
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                // ИСПРАВЛЕНИЕ: Вместо текста вызываем AsyncImage
                AsyncImage(
                    model = selectedImageUri, // Передаем Uri из нашей переменной
                    contentDescription = "Обложка книги",
                    modifier = Modifier.fillMaxSize(),
                    // ContentScale.Crop красиво обрезает и растягивает картинку
                    // по форме нашего прямоугольника со скруглениями
                    contentScale = ContentScale.Crop
                )
            } else {
                // Если картинка еще не выбрана — показываем старый аккуратный текст с плюсиком
                Text(
                    text = "+ Добавить\nобложку",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 3. ПОЛЯ ВВОДА ДАННЫХ (В стиле нашей поисковой строки) ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Название книги
            CustomInputField(
                value = titleText,
                onValueChange = { titleText = it },
                label = "Название книги"
            )

            // Автор книги
            CustomInputField(
                value = authorText,
                onValueChange = { authorText = it },
                label = "Автор"
            )

            CustomInputField(
                value = seriesText,
                onValueChange = { seriesText = it },
                label = "Серия"
            )

            // Количество страниц
            CustomInputField(
                value = pagesText,
                onValueChange = { newValue ->
                    // Разрешаем вводить только цифры
                    if (newValue.all { it.isDigit() }) pagesText = newValue
                },
                label = "Количество страниц",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Описание книги (Многострочное)
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                placeholder = { Text(text = "Описание книги", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Увеличенная высота для текста описания
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color(0xFFE5E5E5),
                    unfocusedContainerColor = Color(0xFFE5E5E5),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // --- 4. КНОПКА СОХРАНЕНИЯ КНИГИ ---
        val isFormValid =
            titleText.isNotEmpty() && authorText.isNotEmpty() && pagesText.isNotEmpty()

        Button(
            onClick = {
                val pagesCount = pagesText.toIntOrNull() ?: 0
                if (isUpdate) {
                    val updatedBook = bookForUpdate.copy(
                        title = titleText,
                        author = authorText,
                        totalPages = pagesCount,
                        description = descriptionText,
                        coverUri = selectedImageUri
                    )
                    onUpdateBookClick(updatedBook)
                } else {
                    onSaveBookClick(
                        AddBookInput(
                            title = titleText,
                            author = authorText,
                            totalPages = pagesCount,
                            description = descriptionText,
                            series = seriesText,
                            idLitres = selectedBook?.id,
                            coverUri = selectedImageUri
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            enabled = isFormValid // Кнопка активна, только если заполнены ключевые поля
        ) {
            Text(
                text = if (isUpdate) "Обновить книгу" else "Добавить книгу",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}