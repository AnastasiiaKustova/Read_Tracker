package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.readtracker.android.domain.entity.stats.StatsEntity
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    // 1. Получить точные сессии для списка внизу экрана за конкретный день (границы в миллисекундах)
    @Query("SELECT * FROM stats WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    fun getActivitiesForPeriod(startTimestamp: Long, endTimestamp: Long): List<StatsWithBookEntity>

    // 2. Экономный подсчет общих метрик (минуты, страницы) за любой период для графиков
    // SQL сделает это мгновенно, не загружая объекты в память телефона
    @Query("""
        SELECT 
            SUM(durationMinutes) as totalMinutes, 
            SUM(pagesRead) as totalPages,
            COUNT(DISTINCT bookId) as totalBooks 
        FROM stats 
        WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp
    """)
    fun getPeriodMetrics(startTimestamp: Long, endTimestamp: Long): PeriodMetricsDto?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivity(stat: StatsEntity)

    @Query("DELETE FROM stats WHERE id = :statId")
    suspend fun deleteNote(statId: String)
}

data class PeriodMetricsDto(
    val totalPages: Int?,
    val totalMinutes: Int?,
    val totalBooks: Int?
)