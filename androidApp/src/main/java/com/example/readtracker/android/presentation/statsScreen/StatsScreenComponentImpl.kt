package com.example.readtracker.android.presentation.statsScreen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.presentation.noteDetailScreen.NoteDetailScreenStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatsScreenComponentImpl @AssistedInject constructor(
    private val storeFactory: StatsScreenStoreFactory,
    @Assisted("componentContext") componentContext: ComponentContext,
) : StatsScreenComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<StatsScreenStore.State> = store.stateFlow

    init {
        // Безопасно подписываемся на события стора, когда экран физически стартует
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            private var scope: CoroutineScope? = null

            override fun onStart() {
                scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()).apply {
                    launch {
                        store.labels.collect { label ->
                            when (label) {

                                else -> {}
                            }
                        }
                    }
                }
            }

            override fun onStop() {
                scope?.cancel()
                scope = null
            }
        })
    }

    override fun onCalendarDayClick(dayNumber: Int) {
        store.accept(StatsScreenStore.Intent.ClickCalendarDay(dayNumber))
    }

    override fun onBarClick(index: Int) {
        store.accept(StatsScreenStore.Intent.ClickBar(index))
    }

    override fun onPeriodSwitch(offset: Int) {
        store.accept(StatsScreenStore.Intent.ChangePeriod(offset))
    }

    override fun onTabSwitch(selectedTab: PeriodTab) {
        store.accept(StatsScreenStore.Intent.ClickTabSwitch(selectedTab))
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("componentContext") componentContext: ComponentContext,
        ): StatsScreenComponentImpl
    }
}