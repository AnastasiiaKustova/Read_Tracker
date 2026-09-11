package com.example.readtracker.android.domain.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.readtracker.android.domain.entity.stats.StatsEntity
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    // 1. Получить точные сессии для списка внизу экрана за конкретный день (границы в миллисекундах)
    @Transaction
    @Query("SELECT * FROM stats WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC")
    suspend fun getActivitiesForPeriod(startTimestamp: Long, endTimestamp: Long): List<StatsWithBookEntity>

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

    @Query("""
    WITH unique_reading_days AS (
        -- 1. Берем только уникальные дни, когда пользователь реально читал страницы
        SELECT DISTINCT (timestamp / 86400000) AS reading_day
        FROM stats
        WHERE pagesRead > 0
    ),
    ordered_days AS (
        -- 2. Нумеруем дни по порядку. При вычитании номера из дня, у непрерывных стриков получится один и тот же результат
        SELECT reading_day,
               (reading_day - ROW_NUMBER() OVER (ORDER BY reading_day ASC)) AS streak_group
        FROM unique_reading_days
    ),
    streak_lengths AS (
        -- 3. Группируем по полученному признаку и считаем длину каждого стрика
        SELECT COUNT(*) AS streak_length
        FROM ordered_days
        GROUP BY streak_group
    )
    -- 4. Вытаскиваем самый максимальный из всех найденных стриков (если записей нет, вернет 0)
    SELECT COALESCE(MAX(streak_length), 0) FROM streak_lengths
""")
    suspend fun getMaxReadingStreak(): Int

    @Query("""
    WITH unique_reading_days AS (
        SELECT DISTINCT (timestamp / 86400000) AS reading_day
        FROM stats
        WHERE pagesRead > 0
    ),
    ordered_days AS (
        SELECT reading_day,
               (reading_day - ROW_NUMBER() OVER (ORDER BY reading_day ASC)) AS streak_group
        FROM unique_reading_days
    ),
    current_streak_group AS (
        -- Ищем группу, к которой принадлежит СЕГОДНЯШНИЙ день (или вчерашний, если сегодня еще не читали)
        SELECT streak_group 
        FROM ordered_days 
        WHERE reading_day = (:currentTimestamp / 86400000) 
           OR reading_day = (:currentTimestamp / 86400000) - 1
        LIMIT 1
    )
    -- Считаем количество дней в этой активной группе
    SELECT COUNT(*) 
    FROM ordered_days 
    WHERE streak_group = (SELECT streak_group FROM current_streak_group)
""")
    suspend fun getCurrentReadingStreak(currentTimestamp: Long): Int
}

data class PeriodMetricsDto(
    val totalPages: Int?,
    val totalMinutes: Int?,
    val totalBooks: Int?
)