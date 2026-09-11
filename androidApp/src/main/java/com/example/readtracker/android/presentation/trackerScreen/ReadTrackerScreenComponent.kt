package com.example.readtracker.android.presentation.trackerScreen

import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import kotlinx.coroutines.flow.StateFlow

interface ReadTrackerScreenComponent {
    val model: StateFlow<ReadTrackerScreenStore.State>
    fun onBackClick()
    fun onFinishReading(newPage: Int, durationMinutes: Int)
}