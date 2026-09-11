package com.example.readtracker.android.presentation.statsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading
import androidx.compose.ui.draw.alpha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreenContent(component: StatsScreenComponent) {

    val state by component.model.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Статистика", color = TextPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (state.selectedTab == PeriodTab.CALENDAR)
                            component.onTabSwitch(PeriodTab.DAYS)
                        else
                            component.onTabSwitch(PeriodTab.CALENDAR)
                    }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Календарь",
                            tint = if (state.selectedTab == PeriodTab.CALENDAR) PrimaryCyan else TextPrimary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Добавить событие",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Проверяем, идет ли сейчас фоновое обновление данных
            val isBackgroundLoading =
                state.screenState is StatsScreenStore.State.ScreenState.Loading

            when (val screenState = state.screenState) {
                StatsScreenStore.State.ScreenState.Error -> CommonError()
                StatsScreenStore.State.ScreenState.Initial -> CommonInitial()

                // ОБЪЕДИНЯЕМ СОСТОЯНИЯ: Если данные уже загружены ИЛИ идет фоновый лоадинг
                // (при условии, что в Стор мы внесли изменение из предыдущего шага и сохраняем старый statsDetails)
                is StatsScreenStore.State.ScreenState.Loaded,
                is StatsScreenStore.State.ScreenState.Loading -> {

                    // Безопасно достаем детали. Если мы в Loading, берем сохраненный oldDetails
                    val details = when (screenState) {
                        is StatsScreenStore.State.ScreenState.Loaded -> screenState.statsDetails
                        is StatsScreenStore.State.ScreenState.Loading -> screenState.oldDetails
                        else -> null
                    }

                    if (details != null) {
                        if (state.selectedTab != PeriodTab.CALENDAR) {
                            PeriodTabs(selectedTab = state.selectedTab, onTabSelected = {
                                component.onTabSwitch(it)
                            })
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Обернем контент в Column с анимацией прозрачности, чтобы скрыть лаг подгрузки
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                            //.alpha(if (isBackgroundLoading) 0.5f else 1.0f) // Экран слегка тускнеет вместо черной вспышки
                        ) {
                            when (state.selectedTab) {
                                PeriodTab.DAYS -> TrackerPeriodContent(
                                    selectedDate = state.selectedDate,
                                    state = details,
                                    selectedTab = state.selectedTab,
                                    onBarClick = { index -> component.onBarClick(index) },
                                    onPeriodSwipe = { offset -> component.onPeriodSwitch(offset) },
                                )

                                PeriodTab.WEEKS -> TrackerPeriodContent(
                                    selectedDate = state.selectedDate,
                                    state = details,
                                    selectedTab = state.selectedTab,
                                    onBarClick = { index -> component.onBarClick(index) },
                                    onPeriodSwipe = { offset -> component.onPeriodSwitch(offset) },
                                )

                                PeriodTab.MONTHS -> TrackerPeriodContent(
                                    selectedDate = state.selectedDate,
                                    state = details,
                                    selectedTab = state.selectedTab,
                                    onBarClick = { index -> component.onBarClick(index) },
                                    onPeriodSwipe = { offset -> component.onPeriodSwitch(offset) },
                                )

                                PeriodTab.CALENDAR -> CalendarContent(
                                    selectedDate = state.selectedDate,
                                    state = details,
                                    onMonthOffset = { offset -> component.onPeriodSwitch(offset) },
                                    onDayClick = { day -> component.onCalendarDayClick(day) }
                                )
                            }
                        }
                    } else {
                        // Если это САМЫЙ первый старт экрана и данных ВООБЩЕ еще нет в памяти — показываем полный лоадер
                        CommonLoading()
                    }
                }
            }
        }
    }
}

// --- ЦВЕТОВАЯ ПАЛИТРА (Тёмная тема под скриншоты) ---
val DarkBackground = Color(0xFF0F0F11)
val SurfaceDark = Color(0xFF1A1A1E)
val PrimaryCyan = Color(0xFF26A6D1)
val AccentPurple = Color(0xFF9C27B0)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8E8E93)