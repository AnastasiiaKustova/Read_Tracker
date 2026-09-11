package com.example.readtracker.android.presentation.statsScreen

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.readtracker.android.domain.entity.PeriodTab
import com.example.readtracker.android.domain.entity.StatsDetailForTrackerPeriod
import com.example.readtracker.android.domain.useCases.GetStatsDetailsUseCase
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.Intent
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.State
import com.example.readtracker.android.presentation.statsScreen.StatsScreenStore.Label
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

interface StatsScreenStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class ClickTabSwitch(val selectedTab: PeriodTab) : Intent
        data class ChangePeriod(val offset: Int) : Intent
        data class ClickCalendarDay(val dayNumber: Int) : Intent
        data class ClickBar(val index: Int) : Intent
    }

    data class State(
        val screenState: ScreenState,
        val selectedDate: Date,             // Базовая дата (текущий день, пн выбранной недели или 1-е число месяца) selectedMonth: Date // Текущий просматриваемый месяц (Стор передает Date)
        val selectedTab: PeriodTab
    ) {
        sealed interface ScreenState {

            data object Initial : ScreenState

            data class Loading(val oldDetails: StatsDetailForTrackerPeriod? = null) : ScreenState

            data object Error : ScreenState

            data class Loaded(
                val statsDetails: StatsDetailForTrackerPeriod,
            ) : ScreenState
        }
    }

    sealed interface Label {
    }
}

class StatsScreenStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val getStatsDetailsUseCase: GetStatsDetailsUseCase,
) {

    fun create(): StatsScreenStore =
        object : StatsScreenStore, Store<Intent, State, Label> by storeFactory.create(
            name = "StatsScreenStore",
            initialState = State(
                screenState = State.ScreenState.Initial,
                selectedTab = PeriodTab.DAYS,
                selectedDate = Date()
            ),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        data class ScreenLoaded(val statsDetails: StatsDetailForTrackerPeriod) : Action

        data object ScreenLoading : Action

        data object ScreenError : Action
    }

    private sealed interface Msg {

        data class ScreenLoaded(
            val statsDetails: StatsDetailForTrackerPeriod,
            val newDate: Date,
            val newTab: PeriodTab
        ) : Msg

        data object ScreenLoading : Msg

        data object ScreenError : Msg
    }

    private inner class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.ScreenLoading)
                try {
                    val defaultDate = Date()
                    val defaultTab = PeriodTab.DAYS
                    val statsDetails = getStatsDetailsUseCase(defaultDate, defaultTab, null)
                    dispatch(Action.ScreenLoaded(statsDetails))
                } catch (e: Exception) {
                    e.printStackTrace() // <-- Добавьте эту строчку, чтобы распечатать ошибку в Logcat
                    val errorMessage = e.localizedMessage // Или поставьте точку останова сюда
                    dispatch(Action.ScreenError)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                Msg.ScreenError -> copy(screenState = State.ScreenState.Error)
                Msg.ScreenLoading -> {
                    val currentDetails = (screenState as? State.ScreenState.Loaded)?.statsDetails
                    copy(screenState = State.ScreenState.Loading(oldDetails = currentDetails))
                }
                is Msg.ScreenLoaded -> copy(screenState = State.ScreenState.Loaded(msg.statsDetails), selectedDate = msg.newDate, selectedTab = msg.newTab)
            }
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.ScreenLoaded -> {
                    val currentState = state()
                    dispatch(Msg.ScreenLoaded(action.statsDetails, currentState.selectedDate, currentState.selectedTab))
                }
                Action.ScreenError -> dispatch(Msg.ScreenError)
                Action.ScreenLoading -> dispatch(Msg.ScreenLoading)
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                // 1. СМЕНА ВКЛАДКИ (Дни / Недели / Месяцы / Календарь)
                is Intent.ClickTabSwitch -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val defaultDate = Date()
                            // Передаем null, так как при переключении таба по умолчанию выбрана ВСЯ неделя/месяц
                            val statsDetails = getStatsDetailsUseCase(defaultDate, intent.selectedTab, null)
                            dispatch(Msg.ScreenLoaded(statsDetails, defaultDate, intent.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }

                // 2. УНИВЕРСАЛЬНЫЙ СДВИГ ПЕРИОДА (Стрелочки календаря или свайп графиков)
                is Intent.ChangePeriod -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val currentState = state()
                            val calendar = Calendar.getInstance().apply { time = currentState.selectedDate }

                            when (currentState.selectedTab) {
                                PeriodTab.DAYS -> calendar.add(Calendar.DAY_OF_MONTH, intent.offset * 7)
                                PeriodTab.WEEKS -> calendar.add(Calendar.WEEK_OF_YEAR, intent.offset * 7)
                                PeriodTab.MONTHS -> calendar.add(Calendar.YEAR, intent.offset)
                                PeriodTab.CALENDAR -> calendar.add(Calendar.MONTH, intent.offset)
                            }
                            val newDate = calendar.time

                            // При сдвиге периода (например, ушли на прошлую неделю) сбрасываем фокус на null (выбрана вся неделя)
                            val statsDetails = getStatsDetailsUseCase(newDate, currentState.selectedTab, null)
                            dispatch(Msg.ScreenLoaded(statsDetails, newDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }

                // 3. КЛИК ПО ДНЮ В СЕТКЕ КАЛЕНДАРЯ
                is Intent.ClickCalendarDay -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val currentState = state()
                            val calendar = Calendar.getInstance().apply {
                                time = currentState.selectedDate
                                set(Calendar.DAY_OF_MONTH, intent.dayNumber)
                            }
                            val newDate = calendar.time

                            // В режиме календаря мы всегда смотрим конкретный день, поэтому передаем null
                            // (Юзкейс сам отфильтрует по дню, если выбран PeriodTab.CALENDAR)
                            val statsDetails = getStatsDetailsUseCase(newDate, currentState.selectedTab, null)
                            dispatch(Msg.ScreenLoaded(statsDetails, newDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }

                // 4. КЛИК НА СТОЛБИК ГРАФИКА (Дни / Месяцы)
                is Intent.ClickBar -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val currentState = state()

                            // Передаем базовую дату, текущий таб И индекс кликнутого столбика (intent.index)
                            // Юзкейс сам сузит выборку до этого дня/месяца и пересчитает цифры!
                            val statsDetails = getStatsDetailsUseCase(
                                date = currentState.selectedDate,
                                periodTab = currentState.selectedTab,
                                selectedBarIndex = intent.index
                            )

                            // Сохраняем состояние. Базовую дату (selectedDate) менять НЕ нужно,
                            // так как мы остаемся в рамках той же недели/года, просто сфокусировались на баре
                            dispatch(Msg.ScreenLoaded(statsDetails, currentState.selectedDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }
            }
        }
    }
}