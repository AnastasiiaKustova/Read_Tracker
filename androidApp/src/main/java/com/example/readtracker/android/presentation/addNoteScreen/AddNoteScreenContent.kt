package com.example.readtracker.android.presentation.addNoteScreen

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.note.AddNoteInput
import com.example.readtracker.android.domain.entity.tag.AddTagInput
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.common.CustomInputField
import java.util.Locale

@Composable
fun AddNoteScreenContent(component: AddNoteScreenComponent) {
    val state by component.model.collectAsState()

    Box {
        when (val screenState = state.screenState) {
            AddNoteScreenStore.State.ScreenState.Error -> CommonError()
            AddNoteScreenStore.State.ScreenState.Initial -> CommonInitial()
            is AddNoteScreenStore.State.ScreenState.Loaded -> {
                CreateNoteScreen(
                    selectedBook = screenState.selectedBook,
                    allAvailableTags = screenState.allAvailableTags,
                    onSelectBookClick = { component.onBookSelectClick() },
                    onAddNewTagClick = { addTagInput -> component.onAddTagClick(addTagInput)},
                    onSaveNoteClick = { addNoteInput -> component.onSaveClick(addNoteInput) }
                )
            }

            AddNoteScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
fun CreateNoteScreen(
    selectedBook: Book?,
    allAvailableTags: Set<Tag>,
    onSelectBookClick: () -> Unit,
    onAddNewTagClick: (AddTagInput) -> Unit,
    onSaveNoteClick: (AddNoteInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Объединяем дефолтные теги из модели и теги пользователя из базы данных
    val combinedTags = remember(allAvailableTags) { allAvailableTags }

    // Состояния полей и окон
    var noteText by remember { mutableStateOf("") }
    var quoteText by remember { mutableStateOf("") }
    var pageText by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<Tag>()) }
    var hasQuote by remember { mutableStateOf(false) }
    var showCreateTagDialog by remember { mutableStateOf(false) }
    var showAllTagsDialog by remember { mutableStateOf(false) }
    var speechTargetField by remember { mutableIntStateOf(0) }

    // Лаунчер голосового ввода
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            if (spokenText.isNotEmpty()) {
                if (speechTargetField == 0) noteText =
                    if (noteText.isEmpty()) spokenText else "$noteText $spokenText"
                else quoteText = if (quoteText.isEmpty()) spokenText else "$quoteText $spokenText"
            }
        }
    }

    val startVoiceInput: (Int) -> Unit = { targetField ->
        speechTargetField = targetField
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите...")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        // Кнопка выбора книги
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE5E5E5))
                .clickable { onSelectBookClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedBook == null) Icon(
                    Icons.Default.Add,
                    null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    selectedBook?.title ?: "Выбрать книгу",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Поле мысли/заметки
        Text(
            "Мои мысли / Заметка",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = { Text("Напишите ваши впечатления о прочитанном...", color = Color.Gray, fontSize = 15.sp) },
            trailingIcon = {
                IconButton(onClick = { startVoiceInput(0) }) {
                    Icon(Icons.Default.Share, null, tint = Color.Gray)
                }
            },
            // РЕШЕНИЕ: Меняем fixed height на heightIn, чтобы поле росло вниз вместе с текстом
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp),
            shape = RoundedCornerShape(16.dp),

            // Позволяет полю расширяться, не ограничивая количество строк
            singleLine = false,
            maxLines = Int.MAX_VALUE,

            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 16.sp),
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

        // Блок Цитаты
        if (!hasQuote) {
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
                        Icons.Default.Add,
                        null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Добавить цитату из книги",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        } else {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Цитата из книги",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD6D6D6))
                                .clickable { hasQuote = false; quoteText = ""; pageText = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = quoteText,
                        onValueChange = { quoteText = it },
                        placeholder = { Text("Текст цитаты...", color = Color.Gray, fontSize = 15.sp) },
                        trailingIcon = {
                            IconButton(onClick = { startVoiceInput(1) }) {
                                Icon(
                                    Icons.Default.Share,
                                    null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        // РЕШЕНИЕ 1: Меняем фиксированные 90.dp на heightIn с минимальным порогом
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp),
                        shape = RoundedCornerShape(16.dp),

                        // РЕШЕНИЕ 2: Разрешаем полю расти бесконечно вниз вслед за текстом
                        singleLine = false,
                        maxLines = Int.MAX_VALUE,

                        // РЕШЕНИЕ 3: Принудительный черный читаемый цвет для вводимого текста
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 16.sp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,       // Черный текст при фокусе
                            unfocusedTextColor = Color.Black,     // Черный текст без фокуса
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value = pageText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) pageText = it },
                        label = "Номер страницы (необязательно)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))

        // Раздел ТЕГИ
        Text("Теги заметки", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))

        TagRowSection(
            combinedTags = combinedTags,
            selectedTags = selectedTags,
            onManageTagsClick = { showAllTagsDialog = true },
            onAddNewTagClick = { showCreateTagDialog = true },
            onTagClick = { tag ->
                selectedTags =
                    if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag
            }
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Кнопки сохранения
        val isNoteValid = selectedBook != null && (noteText.trim().isNotEmpty() || (hasQuote && quoteText.trim().isNotEmpty()))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    onSaveNoteClick(
                        AddNoteInput(
                            if (hasQuote && quoteText.isNotEmpty()) quoteText else null,
                            pageText.toIntOrNull(),
                            noteText,
                            selectedBook!!,
                            selectedTags,
                            false
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp), shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5),
                    disabledContainerColor = Color(0xFFF5F5F5)
                ), enabled = isNoteValid
            ) {
                Text(
                    "Для себя",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isNoteValid) Color.Black else Color.LightGray
                )
            }

            Button(
                onClick = {
                    onSaveNoteClick(
                        AddNoteInput(
                            if (hasQuote && quoteText.isNotEmpty()) quoteText else null,
                            pageText.toIntOrNull(),
                            noteText,
                            selectedBook!!,
                            selectedTags,
                            true
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp), shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    disabledContainerColor = Color(0xFFCCE2FF)
                ), enabled = isNoteValid
            ) {
                Text(
                    "Опубликовать",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Рендеринг локальных диалогов
    if (showCreateTagDialog) {
        CreateTagDialog(
            onDismiss = { showCreateTagDialog = false },
            onConfirm = { title -> onAddNewTagClick(AddTagInput(title = title)); showCreateTagDialog = false })
    }
    if (showAllTagsDialog) {
        SelectTagsDialog(
            allTags = combinedTags,
            selectedTags = selectedTags,
            onDismiss = { showAllTagsDialog = false },
            onTagToggle = { tag ->
                selectedTags =
                    if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag
            })
    }
}