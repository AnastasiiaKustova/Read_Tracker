package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePageDialog(
    totalPages: Int,
    currentPage: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputPageText by remember { mutableStateOf(currentPage.toString()) }

    val isError by remember(inputPageText, totalPages) {
        derivedStateOf {
            val enteredPage = inputPageText.toIntOrNull()
            inputPageText.isNotEmpty() && (enteredPage == null || enteredPage < 0 || enteredPage > totalPages)
        }
    }

    // В Material 3 используем AlertDialogBasic (или просто AlertDialog с контентом)
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        // Отрисовываем контент напрямую в контейнере Surface (базовый слой Material 3)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp), // Фирменное скругление
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 1. Заголовок
                Text(
                    text = "Я на странице",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Поле ввода
                OutlinedTextField(
                    value = inputPageText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                            inputPageText = newValue
                        }
                    },
                    placeholder = { Text(text = "Введите страницу", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color(0xFFE5E5E5),
                        unfocusedContainerColor = Color(0xFFE5E5E5),
                        errorContainerColor = Color(0xFFFCE8E6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        errorBorderColor = Color(0xFFD32F2F)
                    )
                )

                // 3. Сообщение об ошибке
                if (isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Номер страницы не может превышать $totalPages",
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = "Отмена",
                            color = Color.DarkGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    TextButton(
                        onClick = {
                            val pageNumber = inputPageText.toIntOrNull() ?: 0
                            onConfirm(pageNumber)
                        },
                        enabled = !isError && inputPageText.isNotEmpty()
                    ) {
                        Text(
                            text = "ОК",
                            color = if (!isError && inputPageText.isNotEmpty()) Color(0xFF007AFF) else Color.LightGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}