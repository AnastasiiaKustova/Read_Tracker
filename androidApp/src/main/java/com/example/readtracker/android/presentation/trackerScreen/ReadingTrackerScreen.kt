package com.example.readtracker.android.presentation.trackerScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.TrackerMode
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ReadingTrackerScreen(
    startPage: Int, // ЗАГЛУШКА
    onFinishReading: (durationMinutes: Int, newPage: Int) -> Unit,
) {
    var isRunning by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf(TrackerMode.STOPWATCH) }

    var stopwatchSeconds by remember { mutableLongStateOf(0L) }
    var totalTimerSeconds by remember { mutableLongStateOf(1800L) }
    var timerRemainingSeconds by remember { mutableLongStateOf(1800L) }
    var accumulatedSeconds by remember { mutableLongStateOf(0L) }

    var lastEnteredPage by remember { mutableStateOf(startPage) }
    val accumulatedPages = lastEnteredPage - startPage

    var showFinishDialog by remember { mutableStateOf(false) }
    var showSetTimeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var shouldCloseScreen by remember { mutableStateOf(false) }

    val commitCurrentTimeToStack: () -> Unit = {
        if (currentMode == TrackerMode.STOPWATCH) {
            accumulatedSeconds += stopwatchSeconds
            stopwatchSeconds = 0L
        } else {
            val passedTimerSeconds = totalTimerSeconds - timerRemainingSeconds
            accumulatedSeconds += passedTimerSeconds
            timerRemainingSeconds = totalTimerSeconds
        }
    }

    LaunchedEffect(isRunning, currentMode) {
        if (isRunning) {
            while (true) {
                delay(1000)
                if (currentMode == TrackerMode.STOPWATCH) {
                    stopwatchSeconds++
                } else {
                    if (timerRemainingSeconds > 0) {
                        timerRemainingSeconds--
                        if (timerRemainingSeconds == 0L) {
                            isRunning = false
                            accumulatedSeconds += totalTimerSeconds
                            timerRemainingSeconds = totalTimerSeconds
                            shouldCloseScreen = false
                            showFinishDialog = true
                        }
                    }
                }
            }
        }
    }
    val displaySeconds = if (currentMode == TrackerMode.STOPWATCH) stopwatchSeconds else timerRemainingSeconds
    val minutesStr = String.format("%02d", displaySeconds / 60)
    val secondsStr = String.format("%02d", displaySeconds % 60)
    val progressFraction = if (currentMode == TrackerMode.TIMER) timerRemainingSeconds.toFloat() / totalTimerSeconds.toFloat() else 1.0f

    val currentSessionSeconds = if (currentMode == TrackerMode.STOPWATCH) stopwatchSeconds else (totalTimerSeconds - timerRemainingSeconds)
    val totalMinutesDisplay = (accumulatedSeconds + currentSessionSeconds) / 60

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF282828)).padding(24.dp)
    ) {
        IconButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Сброс", tint = Color.White)
        }

        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Накоплено: $totalMinutesDisplay мин", color = Color.White, fontSize = 14.sp)
                    Box(modifier = Modifier.size(1.dp, 16.dp).background(Color.Gray))
                    Text(text = "Страниц: $accumulatedPages", color = Color.White, fontSize = 14.sp)
                }
            }

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 12.dp.toPx()
                    val tickCount = 60
                    for (i in 0 until tickCount) {
                        val angle = (i * (360f / tickCount)) * (Math.PI / 180).toFloat()
                        val tickLength = if (i % 5 == 0) 12.dp.toPx() else 6.dp.toPx()
                        val startX = center.x + radius * cos(angle)
                        val startY = center.y + radius * sin(angle)
                        val endX = center.x + (radius - tickLength) * cos(angle)
                        val endY = center.y + (radius - tickLength) * sin(angle)
                        drawLine(color = Color.White.copy(alpha = 0.3f), start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 2.dp.toPx())
                    }
                    drawArc(color = Color.White, startAngle = -90f, sweepAngle = 360f * progressFraction, useCenter = false, style = Stroke(width = 4.dp.toPx()))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$minutesStr:$secondsStr", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White)
                    ) {
                        if (isRunning) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.size(20.dp)) {
                                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color.Black))
                                Box(modifier = Modifier.fillMaxHeight().weight(1f).background(Color.Black))
                            }
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Старт", tint = Color.Black, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.1f)).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TrackerMode.entries.forEach { mode ->
                    val isSelected = currentMode == mode
                    val tabBg by animateColorAsState(if (isSelected) Color.White else Color.Transparent)
                    val tabTextBy by animateColorAsState(if (isSelected) Color.Black else Color.White)
                    val tabTitle = if (mode == TrackerMode.STOPWATCH) "Секундомер" else "Таймер ${totalTimerSeconds / 60} мин"

                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(tabBg)
                            .clickable {
                                commitCurrentTimeToStack()
                                if (mode == TrackerMode.TIMER) {
                                    if (currentMode == TrackerMode.TIMER) {
                                        if (!isRunning) showSetTimeDialog = true
                                    } else {
                                        currentMode = TrackerMode.TIMER
                                    }
                                } else {
                                    currentMode = TrackerMode.STOPWATCH
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = tabTitle, color = tabTextBy, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Button(
            onClick = {
                isRunning = false
                shouldCloseScreen = true
                showFinishDialog = true
            },
            modifier = Modifier.fillMaxWidth().height(56.dp).align(Alignment.BottomCenter),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(text = "Закончить чтение", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
    if (showFinishDialog) {
        FinishReadingDialog(
            lastKnownPage = lastEnteredPage,
            onDismiss = {
                showFinishDialog = false
                shouldCloseScreen = false
            },
            onConfirm = { typedPage ->
                showFinishDialog = false
                val finalTotalSeconds = accumulatedSeconds + (if (currentMode == TrackerMode.STOPWATCH) stopwatchSeconds else (totalTimerSeconds - timerRemainingSeconds))
                lastEnteredPage = typedPage

                if (shouldCloseScreen) {
                    val finalMinutes = (finalTotalSeconds / 60).toInt().coerceAtLeast(1)
                    onFinishReading(finalMinutes, typedPage)
                } else {
                    accumulatedSeconds = finalTotalSeconds
                    stopwatchSeconds = 0L
                    timerRemainingSeconds = totalTimerSeconds
                }
                shouldCloseScreen = false
            }
        )
    }

    if (showSetTimeDialog) {
        SetTimerDurationDialog(currentMinutes = (totalTimerSeconds / 60).toInt(), onDismiss = { showSetTimeDialog = false }, onConfirm = { totalTimerSeconds = it * 60L; timerRemainingSeconds = totalTimerSeconds; showSetTimeDialog = false })
    }

    if (showResetDialog) {
        ResetConfirmDialog(onDismiss = { showResetDialog = false }, onConfirm = { isRunning = false; stopwatchSeconds = 0L; timerRemainingSeconds = totalTimerSeconds; accumulatedSeconds = 0L; lastEnteredPage = startPage; showResetDialog = false })
    }
}