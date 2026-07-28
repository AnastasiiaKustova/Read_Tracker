package com.example.readtracker.android.domain.repository

import com.example.readtracker.android.domain.entity.MainScreenItem
import kotlinx.coroutines.flow.Flow

interface MainScreenRepository {
    val mainScreenFlow: Flow<MainScreenItem>

}