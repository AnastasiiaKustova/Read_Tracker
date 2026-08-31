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
                    val statsDetails = getStatsDetailsUseCase(defaultDate, defaultTab)
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
                // 1. СМЕНА В КЛАДКИ (Дни / Недели / Месяцы / Календарь)
                is Intent.ClickTabSwitch -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            //val currentState = state()
                            val defaultDate = Date()
                            // Вызываем юзкейс с текущей сохраненной датой, но новым табом
                            val statsDetails = getStatsDetailsUseCase(defaultDate, intent.selectedTab)
                            dispatch(Msg.ScreenLoaded(statsDetails, defaultDate, intent.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }

                // 2. УНИВЕРСАЛЬНЫЙ СДВИГ ПЕРИОДА (Стрелочки календаря или свайп графиков: -1 или +1)
                is Intent.ChangePeriod -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val currentState = state()

                            val calendar = Calendar.getInstance().apply { time = currentState.selectedDate }

                            // В зависимости от масштаба экрана умножаем intent.offset (-1 или +1) на размер страницы данных
                            when (currentState.selectedTab) {
                                PeriodTab.DAYS -> {
                                    // Перелистываем всю неделю целиком (шаг в 7 дней)
                                    calendar.add(Calendar.DAY_OF_MONTH, intent.offset * 7)
                                }
                                PeriodTab.WEEKS -> {
                                    // Перелистываем блок из 7 недель (шаг в 7 недель)
                                    calendar.add(Calendar.WEEK_OF_YEAR, intent.offset * 7)
                                }
                                PeriodTab.MONTHS -> {
                                    // Перелистываем сразу на целый год назад или вперед
                                    calendar.add(Calendar.YEAR, intent.offset)
                                }
                                PeriodTab.CALENDAR -> {
                                    // В режиме календаря стандартно двигаемся ровно по 1 месяцу
                                    calendar.add(Calendar.MONTH, intent.offset)
                                }
                            }
                            val newDate = calendar.time

                            // Запрашиваем новые отмасштабированные данные из Room через Юзкейс
                            val statsDetails = getStatsDetailsUseCase(newDate, currentState.selectedTab)
                            dispatch(Msg.ScreenLoaded(statsDetails, newDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            e.printStackTrace()
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

                            // Меняем число в текущей просматриваемой дате на то, по которому кликнули
                            val calendar = Calendar.getInstance().apply {
                                time = currentState.selectedDate
                                set(Calendar.DAY_OF_MONTH, intent.dayNumber)
                            }
                            val newDate = calendar.time

                            // Перезапрашиваем данные за этот конкретный день
                            val statsDetails = getStatsDetailsUseCase(newDate, currentState.selectedTab)
                            dispatch(Msg.ScreenLoaded(statsDetails, newDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }

                // 4. КЛИК НА СТОЛБИК ГРАФИКА
                is Intent.ClickBar -> {
                    dispatch(Msg.ScreenLoading)
                    scope.launch {
                        try {
                            val currentState = state()
                            val calendar = Calendar.getInstance().apply { time = currentState.selectedDate }

                            // Вычисляем, на какую дату указывает столбик графика
                            val newDate = when (currentState.selectedTab) {
                                PeriodTab.DAYS -> {
                                    // Находим Понедельник текущей недели
                                    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                                    val diffToMonday = if (currentDayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - currentDayOfWeek
                                    calendar.add(Calendar.DAY_OF_MONTH, diffToMonday)
                                    // Сдвигаем на индекс кликнутого бара (0 = Пн, 1 = Вт...)
                                    calendar.add(Calendar.DAY_OF_MONTH, intent.index)
                                    calendar.time
                                }
                                PeriodTab.MONTHS -> {
                                    // Устанавливаем месяц, равный индексу столбика (0 = Январь, 1 = Февраль...)
                                    calendar.set(Calendar.MONTH, intent.index)
                                    calendar.time
                                }
                                else -> currentState.selectedDate // Для остальных табов оставляем базовую дату
                            }

                            // Перезапрашиваем статистику
                            val statsDetails = getStatsDetailsUseCase(newDate, currentState.selectedTab)
                            dispatch(Msg.ScreenLoaded(statsDetails, newDate, currentState.selectedTab))
                        } catch (e: Exception) {
                            dispatch(Msg.ScreenError)
                        }
                    }
                }
            }
        }
    }
}