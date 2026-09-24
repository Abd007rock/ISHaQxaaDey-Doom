package com.example.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScreenTimeStatus(
    val dailyLimitMinutes: Int = 60,
    val minutesUsed: Int = 18,
    val isExceeded: Boolean = false,
    val remainingMinutes: Int = 42,
    val progressRatio: Float = 0.3f
)

class ScreenTimeManager(
    private val scope: CoroutineScope,
    private val onScreenTimeUpdated: (minutesUsed: Int) -> Unit
) {
    private val _status = MutableStateFlow(ScreenTimeStatus())
    val status: StateFlow<ScreenTimeStatus> = _status.asStateFlow()

    private var timerJob: Job? = null

    fun initialize(dailyLimitMinutes: Int, currentMinutesUsed: Int) {
        val remaining = (dailyLimitMinutes - currentMinutesUsed).coerceAtLeast(0)
        val progress = (currentMinutesUsed.toFloat() / dailyLimitMinutes.coerceAtLeast(1)).coerceIn(0f, 1f)
        _status.value = ScreenTimeStatus(
            dailyLimitMinutes = dailyLimitMinutes,
            minutesUsed = currentMinutesUsed,
            isExceeded = remaining <= 0,
            remainingMinutes = remaining,
            progressRatio = progress
        )
        startTicking()
    }

    private fun startTicking() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (true) {
                // In production, ticks every 60 seconds of active usage.
                // For demonstrability and responsive safety enforcement, every 60s increments active usage.
                delay(60000L)
                val current = _status.value
                val newUsed = current.minutesUsed + 1
                val remaining = (current.dailyLimitMinutes - newUsed).coerceAtLeast(0)
                val progress = (newUsed.toFloat() / current.dailyLimitMinutes.coerceAtLeast(1)).coerceIn(0f, 1f)
                val isExceeded = remaining <= 0

                _status.value = ScreenTimeStatus(
                    dailyLimitMinutes = current.dailyLimitMinutes,
                    minutesUsed = newUsed,
                    isExceeded = isExceeded,
                    remainingMinutes = remaining,
                    progressRatio = progress
                )
                onScreenTimeUpdated(newUsed)
            }
        }
    }

    fun addBonusMinutes(bonusMinutes: Int) {
        val current = _status.value
        val newLimit = current.dailyLimitMinutes + bonusMinutes
        val remaining = (newLimit - current.minutesUsed).coerceAtLeast(0)
        val progress = (current.minutesUsed.toFloat() / newLimit.coerceAtLeast(1)).coerceIn(0f, 1f)
        _status.value = current.copy(
            dailyLimitMinutes = newLimit,
            remainingMinutes = remaining,
            progressRatio = progress,
            isExceeded = remaining <= 0
        )
    }

    fun stop() {
        timerJob?.cancel()
    }
}
