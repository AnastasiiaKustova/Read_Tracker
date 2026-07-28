package com.example.readtracker.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.readtracker.android.domain.entity.MainScreenItem
import com.example.readtracker.android.domain.entity.MainScreenItem.Companion.default
import com.example.readtracker.android.domain.repository.MainScreenRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson
) : MainScreenRepository {

    private val repositoryJob = SupervisorJob()
    private val repositoryScope = CoroutineScope(Dispatchers.IO + repositoryJob)

    private val _gameState = MutableStateFlow(default())
    override val mainScreenFlow: Flow<MainScreenItem> = _gameState.asStateFlow()

    init {
        repositoryScope.launch {

        }
    }
}