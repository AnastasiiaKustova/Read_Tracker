package com.example.readtracker.android.domain.entity.readTracker

import com.example.readtracker.android.domain.entity.TrackerMode

data class ReadingTrackerState(
    val isRunning: Boolean = false,
    val currentMode: TrackerMode = TrackerMode.STOPWATCH,
    val elapsedTimeSeconds: Long = 0L,     // Сколько секунд прошло (для секундомера)
    val totalTimerDurationSeconds: Long = 1800L, // Установленное время таймера (например, 30 мин)
    val remainingTimerSeconds: Long = 1800L,     // Сколько секунд осталось (для таймера)
    val showFinishDialog: Boolean = false  // Показ окна ввода страниц
)