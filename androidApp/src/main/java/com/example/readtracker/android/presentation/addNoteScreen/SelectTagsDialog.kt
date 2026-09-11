package com.example.readtracker.android.presentation.addNoteScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.tag.Tag

@Composable
fun SelectTagsDialog(
    allTags: Set<Tag>,
    selectedTags: Set<Tag>,
    onDismiss: () -> Unit,
    onTagToggle: (Tag) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Выберите теги для заметки", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allTags.forEach { tag ->
                        val isChecked = selectedTags.contains(tag)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTagToggle(tag) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onTagToggle(tag) },
                                colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = tag.title, fontSize = 15.sp, color = Color.Black)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Готово", color = Color(0xFF007AFF), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}