package com.example.readtracker.android.presentation.trackerScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.readtracker.android.domain.entity.stats.AddStatsInput
import com.example.readtracker.android.presentation.common.CommonError
import com.example.readtracker.android.presentation.common.CommonInitial
import com.example.readtracker.android.presentation.common.CommonLoading

@Composable
fun ReadTrackerScreenContent(component: ReadTrackerScreenComponent) {

    val state by component.model.collectAsState()

    Box {
        when (val screenState = state.screenState) {
            ReadTrackerScreenStore.State.ScreenState.Error -> CommonError()
            ReadTrackerScreenStore.State.ScreenState.Initial -> CommonInitial()
            is ReadTrackerScreenStore.State.ScreenState.Loaded -> {
                ReadingTrackerScreen(
                    startPage = screenState.book.currentPage,
                    onFinishReading = { durationMinutes: Int, newPage: Int ->
                        component.onFinishReading(
                            newPage = newPage,
                            durationMinutes = durationMinutes,
                            )
                    },
                )
            }

            ReadTrackerScreenStore.State.ScreenState.Loading -> CommonLoading()
        }
    }
}