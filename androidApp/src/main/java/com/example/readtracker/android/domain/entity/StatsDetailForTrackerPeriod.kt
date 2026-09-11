package com.example.readtracker.android.domain.entity

import com.example.readtracker.android.domain.entity.stats.ReadStat


data class StatsDetailForTrackerPeriod(
    val selectedBarIndex: Int,          // Индекс выбранного столбика на графике (0..6 для дней/недель, 0..11 для месяцев)
    val totalTimeStr: String,           // Сумма за весь период на графике
    val totalPagesStr: String,          // Сумма страниц
    val totalBooksStr: String,          // Сумма книг
    val chartHeights: List<Float>,      // Высота полос от 0.0f до 1.0f (7 элементов для дней/недель, 12 для месяцев)
    val activities: List<ReadStat>,      // Список сессий. Сюда Room отдает данные в зависимости от выбранного столбика!

    val selectedDay: Int?,                  // Какое число сейчас выбрано (Int или null)
    val activeDays: Set<Int>,               // Сет чисел месяца, в которые читали: setOf(1, 3, 5...)
)
