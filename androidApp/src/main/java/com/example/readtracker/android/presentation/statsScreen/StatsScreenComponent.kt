package com.example.readtracker.android.presentation.statsScreen

import com.example.readtracker.android.domain.entity.PeriodTab
import kotlinx.coroutines.flow.StateFlow

interface StatsScreenComponent {
    val model: StateFlow<StatsScreenStore.State>

    fun onCalendarDayClick(dayNumber: Int)

    fun onBarClick(index: Int)

    fun onPeriodSwitch(offset: Int)

    fun onTabSwitch(selectedTab: PeriodTab)
}