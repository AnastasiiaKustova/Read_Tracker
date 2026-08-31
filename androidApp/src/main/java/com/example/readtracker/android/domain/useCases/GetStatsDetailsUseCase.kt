package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.repository.StatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class GetStatsDetailsUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    suspend operator fun invoke(date: Date, periodTab: PeriodTab): StatsDetailForTrackerPeriod =
        withContext(Dispatchers.IO) {
            repository.getStatistics(date, periodTab)
        }
}