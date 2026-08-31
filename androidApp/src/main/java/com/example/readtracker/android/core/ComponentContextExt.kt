package com.example.readtracker.android.core

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

fun ComponentContext.componentScope() : CoroutineScope = CoroutineScope(
    Dispatchers.Main.immediate + SupervisorJob()
).apply {
    lifecycle.doOnDestroy { cancel() }
}

fun Int.formatWithSpace(): String {
    return try {
        val formatter = NumberFormat.getInstance(Locale.getDefault())
        formatter.isGroupingUsed = true
        formatter.maximumFractionDigits = 0
        val formatted = formatter.format(this)
            .replace(" ", "\u202f")
        "$formatted"
    } catch (e: Exception) {
        "0"
    }
}

fun Long.formatTimestamp(pattern: String = "d MMMM yyyy, HH:mm"): String {
    // 1. Создаем классический объект Date прямо из миллисекунд (this)
    val date = Date(this)

    // 2. Создаем форматтер с учетом языка системы устройства
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())

    // 3. Возвращаем готовую красивую строку
    return formatter.format(date)
}