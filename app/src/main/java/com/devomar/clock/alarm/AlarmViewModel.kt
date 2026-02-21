package com.devomar.clock.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.devomar.clock.alarm.model.Alarm
import com.devomar.clock.data.AppDatabase
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).alarmDao()
    val alarms: LiveData<List<Alarm>> = dao.getAllAlarms()

    fun addAlarm(alarm: Alarm) = viewModelScope.launch {
        val id = dao.insert(alarm).toInt()
        val saved = alarm.copy(id = id)
        if (saved.isEnabled) AlarmScheduler.schedule(getApplication(), saved)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        dao.update(alarm)
        AlarmScheduler.cancel(getApplication(), alarm)
        if (alarm.isEnabled) AlarmScheduler.schedule(getApplication(), alarm)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        AlarmScheduler.cancel(getApplication(), alarm)
        dao.delete(alarm)
    }

    fun toggleAlarm(alarm: Alarm, enabled: Boolean) = viewModelScope.launch {
        val updated = alarm.copy(isEnabled = enabled)
        dao.update(updated)
        if (enabled) AlarmScheduler.schedule(getApplication(), updated)
        else AlarmScheduler.cancel(getApplication(), updated)
    }
}
