package com.devomar.clock.timer

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    enum class State { IDLE, RUNNING, PAUSED, DONE }

    private val _state = MutableLiveData(State.IDLE)
    val state: LiveData<State> = _state

    private val _remainingMs = MutableLiveData(0L)
    val remainingMs: LiveData<Long> = _remainingMs

    private val _totalMs = MutableLiveData(0L)
    val totalMs: LiveData<Long> = _totalMs

    private var endTime = 0L
    private var pausedRemain = 0L

    fun setDuration(hours: Int, minutes: Int, seconds: Int) {
        val ms = (hours * 3600L + minutes * 60L + seconds) * 1000L
        _totalMs.value = ms
        _remainingMs.value = ms
        pausedRemain = ms
        _state.value = State.IDLE
    }

    fun start() {
        val remain = when (_state.value) {
            State.PAUSED -> pausedRemain
            State.IDLE   -> _totalMs.value ?: 0L
            else         -> return
        }
        if (remain <= 0L) return
        endTime = SystemClock.elapsedRealtime() + remain
        _state.value = State.RUNNING
    }

    fun pause() {
        pausedRemain = endTime - SystemClock.elapsedRealtime()
        _remainingMs.value = pausedRemain.coerceAtLeast(0L)
        _state.value = State.PAUSED
    }

    fun reset() {
        pausedRemain = _totalMs.value ?: 0L
        _remainingMs.value = pausedRemain
        _state.value = State.IDLE
    }

    fun tick() {
        if (_state.value != State.RUNNING) return
        val remaining = endTime - SystemClock.elapsedRealtime()
        if (remaining <= 0L) {
            _remainingMs.value = 0L
            _state.value = State.DONE
        } else {
            _remainingMs.value = remaining
        }
    }
}
