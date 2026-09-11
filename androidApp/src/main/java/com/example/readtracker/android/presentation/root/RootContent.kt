package com.example.readtracker.android.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.example.readtracker.android.domain.entity.BottomTab
import com.example.readtracker.android.presentation.addBookScreen.AddBookScreenContent
import com.example.readtracker.android.presentation.addCollectionScreen.AddCollectionScreenContent
import com.example.readtracker.android.presentation.addNoteScreen.AddNoteScreenContent
import com.example.readtracker.android.presentation.bookDetailScreen.BookDetailScreenContent
import com.example.readtracker.android.presentation.bookListScreen.BookListScreenContent
import com.example.readtracker.android.presentation.mainScreen.MainScreenContent
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenContent
import com.example.readtracker.android.presentation.noteScreen.NoteScreenContent
import com.example.readtracker.android.presentation.profileScreen.ProfileScreenContent
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenComponentImpl
import com.example.readtracker.android.presentation.searchBookScreen.SearchBookScreenContent
import com.example.readtracker.android.presentation.statsScreen.StatsScreenContent
import com.example.readtracker.android.presentation.trackerScreen.ReadTrackerScreenContent
import com.example.readtracker.android.presentation.ui.AppTheme

@Composable
fun RootContent(component: RootComponent) {
    // 1. Подписываемся на состояние стека экранов Decompose
    val childStack by component.stack.subscribeAsState()
    val activeChild = childStack.active.instance

    // 2. Определяем, какая вкладка нижнего меню соответствует активному экрану
    val currentTab = when (activeChild) {
        is RootComponent.Child.MainScreen -> BottomTab.MAIN
        is RootComponent.Child.BookDetail -> BottomTab.MAIN
        is RootComponent.Child.AddBook -> BottomTab.MAIN
        is RootComponent.Child.AddNote -> BottomTab.NOTES
        is RootComponent.Child.NoteScreen -> BottomTab.NOTES
        is RootComponent.Child.NoteDetail -> BottomTab.NOTES
        is RootComponent.Child.StatsScreen -> BottomTab.STATS
        is RootComponent.Child.ProfileScreen -> BottomTab.PROFILE
        is RootComponent.Child.BookList -> BottomTab.MAIN
        is RootComponent.Child.AddCollection -> BottomTab.MAIN
        is RootComponent.Child.SearchBook -> BottomTab.MAIN
        is RootComponent.Child.ReadTrackerScreen -> BottomTab.STATS
    }

    AppTheme {
        // 3. Используем Scaffold для правильного размещения контента и нижнего меню
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White, // Белый фон меню
                    tonalElevation = 8.dp,
                    // Закругляем верхние углы меню, как на макете
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    // Перебираем все элементы нашего Enum вкладок (создали в прошлых шагах)
                    BottomTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                // Вызываем метод переключения вкладок в вашем RootComponent
                                component.onTabSelected(tab)
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) Color(0xFF007AFF) else Color.Black
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) Color(0xFF007AFF) else Color.Black
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFE5E5E5) // Серая овальная подложка активной иконки
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            // 4. Основной контейнер для контента экранов
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Если открыты детали, обнуляем отступы, чтобы экран занял 100% пространства телефона
                    .padding(if (activeChild is RootComponent.Child.BookDetail) PaddingValues(0.dp) else innerPadding)
            ) {
                Children(stack = component.stack) {
                    when (val instance = it.instance) {
                        is RootComponent.Child.MainScreen -> MainScreenContent(component = instance.component)
                        is RootComponent.Child.NoteScreen -> NoteScreenContent(component = instance.component)
                        is RootComponent.Child.StatsScreen -> StatsScreenContent(component = instance.component)
                        is RootComponent.Child.ProfileScreen -> ProfileScreenContent(component = instance.component)
                        is RootComponent.Child.BookDetail -> BookDetailScreenContent(component = instance.component)
                        is RootComponent.Child.NoteDetail -> NoteDetailScreenContent(component = instance.component)
                        is RootComponent.Child.BookList -> BookListScreenContent(component = instance.component)
                        is RootComponent.Child.AddBook -> AddBookScreenContent(component = instance.component)
                        is RootComponent.Child.AddNote -> AddNoteScreenContent(component = instance.component)
                        is RootComponent.Child.AddCollection -> AddCollectionScreenContent(component = instance.component)
                        is RootComponent.Child.SearchBook -> SearchBookScreenContent(component = instance.component)
                        is RootComponent.Child.ReadTrackerScreen -> ReadTrackerScreenContent(component = instance.component)
                    }
                }
            }
        }
    }
}