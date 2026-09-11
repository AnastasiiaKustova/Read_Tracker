package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import com.example.readtracker.android.domain.model.PeriodMetricsDto

interface StatsRepository {
    suspend fun addActivity(addStatsInput: AddStatsInput)
    suspend fun getActivitiesForPeriod(startTime: Long, endTime: Long): List<StatsWithBookEntity>
    suspend fun getPeriodMetrics(startTime: Long, endTime: Long): PeriodMetricsDto?
}