package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import java.util.Date

interface StatsRepository {
    suspend fun addActivity(addStatsInput: AddStatsInput)
    suspend fun getStatistics(date: Date, periodTab: PeriodTab): StatsDetailForTrackerPeriod
}