package com.example.readtracker.android.presentation.trackerScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FinishReadingDialog(
    lastKnownPage: Int, // Передаем последнюю страницу (стартовую или прошлую введённую)
    onDismiss: () -> Unit,
    onConfirm: (currentPage: Int) -> Unit
) {
    // Поле ввода сразу заполнено текущей страницей, пользователю нужно просто прибавить к ней свои изменения
    var currentPageText by remember(lastKnownPage) {
        mutableStateOf(if (lastKnownPage > 0) lastKnownPage.toString() else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val typedPage = currentPageText.toIntOrNull() ?: 0
                    onConfirm(typedPage)
                },
                // Кнопка активна, если ввели число и оно не меньше, чем было
                enabled = currentPageText.isNotEmpty() && (currentPageText.toIntOrNull() ?: 0) >= lastKnownPage,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Сохранить", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.DarkGray)
            }
        },
        title = { Text("Отличная сессия!", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "На какой странице вы сейчас остановились?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentPageText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) currentPageText = it },
                    label = { Text("Номер текущей страницы") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    )
}