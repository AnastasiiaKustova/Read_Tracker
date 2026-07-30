package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    tagSet: Set<Tag>,
    selectedTagId: String?,
    onTagSelect: (String?) -> Unit,
    onAddNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 1. Строка заголовка: "Заметки" + Кнопка "+"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Заметки",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = onAddNoteClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить заметку",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Поле поиска (овальное, серое, без индикатора фокуса)
        NotesSearchField(searchQuery, onSearchQueryChange)

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Строка тегов
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Первый элемент — статичная надпись "Теги"
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E5E5)) // Серый фон
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { onTagSelect(null) } // Сброс фильтра при клике на "Теги"
                ) {
                    Text(
                        text = "Теги",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Рендерим остальные теги из вашего Set<Tag>
            items(tagSet.toList(), key = { it.id }) { tag ->
                val isSelected = tag.id == selectedTagId

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color.Black else Color(0xFFE5E5E5)) // Черный, если выбран
                        .clickable { onTagSelect(tag.id) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tag.title,
                        fontSize = 14.sp,
                        // Белый текст на черном фоне, черный на сером
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}