package com.example.readtracker.android.data.repository

import com.example.readtracker.android.data.mapper.toEntity
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import com.example.readtracker.android.domain.entity.stats.toReadStat
import com.example.readtracker.android.domain.model.PeriodMetricsDto
import com.example.readtracker.android.domain.model.StatsDao
import com.example.readtracker.android.domain.repository.StatsRepository
import java.util.UUID
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao
) : StatsRepository {
    override suspend fun addActivity(addStatsInput: AddStatsInput) {
        val generatedId = UUID.randomUUID().toString()
        // Сохраняем точный текущий таймстамп создания записи (число Long)
        val createdAt = System.currentTimeMillis()
        val finalActivity = addStatsInput.toReadStat(generatedId, createdAt)

        // Вызываем метод вставки в Room
        statsDao.insertActivity(finalActivity.toEntity())
    }

    override suspend fun getActivitiesForPeriod(startTime: Long, endTime: Long): List<StatsWithBookEntity> {
        return statsDao.getActivitiesForPeriod(startTime, endTime)
    }

    override suspend fun getPeriodMetrics(startTime: Long, endTime: Long): PeriodMetricsDto? {
        return statsDao.getPeriodMetrics(startTime, endTime)
    }
}