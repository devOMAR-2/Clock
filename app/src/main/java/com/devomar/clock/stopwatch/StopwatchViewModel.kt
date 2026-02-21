package com.devomar.clock.stopwatch

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StopwatchViewModel : ViewModel() {

    enum class State { IDLE, RUNNING, PAUSED }

    private val _state = MutableLiveData(State.IDLE)
    val state: LiveData<State> = _state

    private val _elapsedMs = MutableLiveData(0L)
    val elapsedMs: LiveData<Long> = _elapsedMs

    private val _laps = MutableLiveData<List<Long>>(emptyList())
    val laps: LiveData<List<Long>> = _laps

    private var startTime = 0L   // SystemClock.elapsedRealtime() at last start
    private var pausedMs = 0L    // accumulated ms before last pause

    fun start() {
        startTime = SystemClock.elapsedRealtime()
        _state.value = State.RUNNING
    }

    fun pause() {
        pausedMs += SystemClock.elapsedRealtime() - startTime
        _state.value = State.PAUSED
        _elapsedMs.value = pausedMs
    }

    fun reset() {
        startTime = 0L
        pausedMs = 0L
        _elapsedMs.value = 0L
        _laps.value = emptyList()
        _state.value = State.IDLE
    }

    fun lap() {
        val current = _laps.value?.toMutableList() ?: mutableListOf()
        current.add(0, currentElapsed())
        _laps.value = current
    }

    fun tick() {
        if (_state.value == State.RUNNING) {
            _elapsedMs.value = currentElapsed()
        }
    }

    private fun currentElapsed(): Long =
        if (_state.value == State.RUNNING)
            pausedMs + SystemClock.elapsedRealtime() - startTime
        else
            pausedMs
}
