package com.example.readtracker.android.domain.useCases

import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.domain.repository.StatsRepository
import javax.inject.Inject

class AddActivityUseCase @Inject constructor(
    private val repository: StatsRepository
) {
    suspend operator fun invoke(addStatsInput: AddStatsInput) = repository.addActivity(addStatsInput)
}