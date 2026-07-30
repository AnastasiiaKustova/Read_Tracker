package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                text = "Поиск по цитате, автору и названию",
                color = Color.Gray,
                fontSize = 15.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp), // Чуть увеличили высоту для лучшего центрирования текста внутри
        shape = RoundedCornerShape(26.dp), // Радиус равен половине высоты для идеального овала
        colors = TextFieldDefaults.colors(
            // --- НАСТРОЙКА ЦВЕТА ВВОДИМОГО ТЕКСТА ---
            focusedTextColor = Color.Black,       // Цвет текста при фокусе
            unfocusedTextColor = Color.Black,     // Цвет текста без фокуса
            disabledTextColor = Color.Gray,

            // Настройки фона
            focusedContainerColor = Color(0xFFE5E5E5),
            unfocusedContainerColor = Color(0xFFE5E5E5),
            disabledContainerColor = Color(0xFFE5E5E5),

            // Убираем подчеркивание
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}