package com.example.readtracker.android.presentation.addNoteScreen

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.AddNoteInput
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.Tag
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.common.CustomInputField
import com.example.readtracker.android.presentation.common.TagUtilityButton

@Composable
fun AddNoteScreenContent(component: AddNoteScreenComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            AddNoteScreenStore.State.ScreenState.Error -> CommonError()
            AddNoteScreenStore.State.ScreenState.Initial -> CommonInitial()
            is AddNoteScreenStore.State.ScreenState.Loaded -> {
                CreateNoteScreen(
                    allAvailableTags = screenState.allAvailableTags,
                    onManageTagsClick = {},
                    onAddNewTagClick = {},
                    onSaveNoteClick = { addNoteInput -> component.onSaveClick(addNoteInput)}
                )
            }
            AddNoteScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
fun CreateNoteScreen(
    allAvailableTags: Set<Tag>,        // Все созданные пользователем теги из базы данных
    onManageTagsClick: () -> Unit,     // Клик по кнопке "Посмотреть все теги"
    onAddNewTagClick: () -> Unit,      // Клик по кнопке "Добавить тег"
    onSaveNoteClick: (addNoteInput: AddNoteInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Состояния полей ввода
    var noteText by remember { mutableStateOf("") }
    var quoteText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }

    // Множество выбранных тегов для этой заметки
    var selectedTags by remember { mutableStateOf(setOf<Tag>()) }

    var selectedBook by remember { mutableStateOf(Book.test()) }

    // Триггер: прикреплена ли к заметке цитата из книги
    var hasQuote by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // --- 1. ОСНОВНОЙ ТЕКСТ ЗАМЕТКИ (МЫСЛИ ПОЛЬЗОВАТЕЛЯ) ---
        Text(
            text = "Мои мысли / Заметка",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = {
                Text(
                    text = "Напишите ваши впечатления о прочитанном...",
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
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

        Spacer(modifier = Modifier.height(20.dp))

        // --- 2. ДИНАМИЧЕСКИЙ БЛОК ЦИТАТЫ И СТРАНИЦЫ ---
        if (!hasQuote) {
            // Если цитаты нет — показываем аккуратную серую кнопку добавления
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE5E5E5))
                    .clickable { hasQuote = true },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Добавить цитату из книги",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        } else {
            // Если пользователь нажал "Добавить" — разворачиваем поля ввода цитаты и страницы
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5E5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Ряд шапки цитаты: Кнопка-крестик удаления в правом углу
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Цитата из книги",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        // Лаконичная кнопка-крестик без паразитных кругов
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD6D6D6))
                                .clickable {
                                    hasQuote = false
                                    quoteText = "" // Сбрасываем текст при удалении
                                    pageText = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Удалить цитату",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Поле ввода самого текста цитаты
                    OutlinedTextField(
                        value = quoteText,
                        onValueChange = { quoteText = it },
                        placeholder = { Text(text = "Текст цитаты...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color(0xFFF5F5F5), // Чуть светлее фон внутри карточки
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Поле ввода номера страницы
                    CustomInputField(
                        value = pageText,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) pageText = newValue
                        },
                        label = "Номер страницы (необязательно)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- 3. БЛОК ВЫБОРА И УПРАВЛЕНИЯ ТЕГАМИ ---
        Text(
            text = "Теги заметки",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Горизонтальная лента тегов и кнопок управления
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка 1: Посмотреть все теги (иконка списка)
            item {
                TagUtilityButton(icon = Icons.Default.List, onClick = onManageTagsClick)
            }

            // Кнопка 2: Создать новый тег (иконка плюса)
            item {
                TagUtilityButton(icon = Icons.Default.Add, onClick = onAddNewTagClick)
            }

            // Рендерим список всех доступных тегов приложения
            items(allAvailableTags.toList(), key = { it.id }) { tag ->
                val isSelected = selectedTags.contains(tag)

                // Тег-чипсы в нашем фирменном стиле
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        // Меняем фон: черный если выбран, серый если не выбран
                        .background(if (isSelected) Color.Black else Color(0xFFE5E5E5))
                        .clickable {
                            // Переключаем стейт выбора: если выбран — удаляем, если нет — добавляем в сет
                            selectedTags =
                                if (isSelected) selectedTags - tag else selectedTags + tag
                        }
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tag.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else Color.Black
                    )
                    // Если тег выбран — рисуем крестик для его быстрого удаления из заметки
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Убрать тег",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(40.dp))
// --- 4. ДВЕ КНОПКИ СОХРАНЕНИЯ (Для себя / Опубликовать) ---
        // Заметка валидна, если написан хотя бы текст мыслей или текст цитаты
        val isNoteValid = noteText.trim().isNotEmpty() || (hasQuote && quoteText.trim().isNotEmpty())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(), // Учитывает системную полоску навигации
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Отступ между кнопками
        ) {
            // Кнопка 1: Сохранить для себя (занимает 50% ширины)
            Button(
                onClick = {
                    val pageNumber = pageText.toIntOrNull()
                    val finalQuote = if (hasQuote && quoteText.isNotEmpty()) quoteText else null
                    // Передаем статус публикации false (только для себя)
                    onSaveNoteClick(AddNoteInput(
                        quoteText = finalQuote,
                        pageNumber = pageNumber,
                        userComment = noteText,
                        book = selectedBook,
                        tags = selectedTags,
                        isPublic = false)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                // Используем фирменный серый цвет для приватного действия
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5),
                    disabledContainerColor = Color(0xFFF5F5F5)
                ),
                enabled = isNoteValid
            ) {
                Text(
                    text = "Для себя",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNoteValid) Color.Black else Color.LightGray
                )
            }

            // Кнопка 2: Сохранить и опубликовать (занимает 50% ширины)
            Button(
                onClick = {
                    val pageNumber = pageText.toIntOrNull()
                    val finalQuote = if (hasQuote && quoteText.isNotEmpty()) quoteText else null
                    // Передаем статус публикации true (опубликовать)
                    onSaveNoteClick(
                        AddNoteInput(
                            quoteText = finalQuote,
                            pageNumber = pageNumber,
                            userComment = noteText,
                            book = selectedBook,
                            tags = selectedTags,
                            isPublic = true)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                // Яркий синий цвет для публичного действия
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    disabledContainerColor = Color(0xFFCCE2FF)
                ),
                enabled = isNoteValid
            ) {
                Text(
                    text = "Опубликовать",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun CreateNoteScreenTest(){
    CreateNoteScreen(
        setOf(Tag.test1(), Tag.test2()),
        {},
        {},
        { addNoteInput: AddNoteInput ->
        },
    )
}