package com.example.readtracker.android.presentation.addCollectionScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.AddCollectionInput
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import com.example.readtracker.android.presentation.common.CustomInputField

@Composable
fun AddCollectionScreenContent(component: AddCollectionScreenComponent) {
    val state by component.model.collectAsState()

    Box{
        when(val screenState = state.screenState){
            AddCollectionScreenStore.State.ScreenState.Error -> CommonError()
            AddCollectionScreenStore.State.ScreenState.Initial -> CommonInitial()
            is AddCollectionScreenStore.State.ScreenState.Loaded -> {
                AddCollectionScreen(
                    selectedBooks = screenState.selectedBooks,
                    onAddBooksClick = { component.onAddBooks() },
                    onRemoveBookClick = { bookId ->
                        component.onRemoveBookClick(bookId)
                    },
                    onCreateCollectionClick = { addCollectionInput ->
                        component.onSaveClick(addCollectionInput)
                    },
                )
            }
            AddCollectionScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}

@Composable
fun AddCollectionScreen(
    selectedBooks: Set<Book>,
    onAddBooksClick: () -> Unit, // Лямбда открытия экрана книг с мультивыбором
    onCreateCollectionClick: (addCollectionInput: AddCollectionInput) -> Unit,
    onRemoveBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние для названия коллекции
    var collectionTitle by remember { mutableStateOf("") }

    // Сет выбранных книг (в реальном приложении этот список будет прилетать из стейта компонента/MVI)
    // Пока сделаем локальный стейт для демонстрации верстки
    //var selectedBooks by remember { mutableStateOf(books) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- 1. ШАПКА И ПОЛЕ ВВОДА НАЗВАНИЯ ---
        Text(
            text = "Новая коллекция",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomInputField(
            value = collectionTitle,
            onValueChange = { collectionTitle = it },
            label = "Название коллекции"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- 2. КНОПКА ДОБАВЛЕНИЯ КНИГ ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE5E5E5)) // Фирменный серый цвет
                .clickable { onAddBooksClick() },
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
                    text = "Добавить книги в коллекцию",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Выбранные книги (${selectedBooks.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. ПРОКРУЧИВАЕМЫЙ СПИСОК ВЫБРАННЫХ КНИГ ---
        // weight(1f) заставит список занять всё свободное пространство, вытолкнув кнопку вниз
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (selectedBooks.isEmpty()) {
                item {
                    Text(
                        text = "В этой коллекции пока нет книг. Нажмите кнопку выше, чтобы добавить их.",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            items(selectedBooks.toList(), key = { it.id }) { book ->
                // Каждая выбранная книга — это компактная горизонтальная плашка
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F5F5)) // Чуть более светлый серый для контраста
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = book.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = book.author,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Маленькая круглая кнопка удаления книги из списка (без паразитных кругов)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5E5))
                            .clickable {
                                // Исключаем книгу из выбранных
                                onRemoveBookClick(book.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Удалить из коллекции",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // --- 4. КНОПКА СОХРАНЕНИЯ КОЛЛЕКЦИИ ---
        // Кнопка активна, только если введено название
        val isFormValid = collectionTitle.trim().isNotEmpty()

        Button(
            onClick = {
                onCreateCollectionClick(AddCollectionInput(collectionTitle, selectedBooks))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color(0xFFE5E5E5)
            ),
            enabled = isFormValid
        ) {
            Text(
                text = "Создать коллекцию",
                fontSize = 16.sp,
                color = if (isFormValid) Color.White else Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun AddCollectionScreenTest(){
    AddCollectionScreen(
        emptySet(),
        {},
        { addCollectionInput: AddCollectionInput -> },
        {}
    )
}