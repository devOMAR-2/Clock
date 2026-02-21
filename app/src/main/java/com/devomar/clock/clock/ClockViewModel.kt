package com.devomar.clock.clock

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devomar.clock.util.PrefsManager
import java.util.TimeZone

class ClockViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)

    private val _isAnalog = MutableLiveData(true)
    val isAnalog: LiveData<Boolean> = _isAnalog

    private val _selectedTimeZone = MutableLiveData(
        TimeZone.getTimeZone(prefs.selectedTimezoneId)
    )
    val selectedTimeZone: LiveData<TimeZone> = _selectedTimeZone

    fun toggleMode() {
        _isAnalog.value = !(_isAnalog.value ?: true)
    }

    fun setTimeZone(timeZoneId: String) {
        prefs.selectedTimezoneId = timeZoneId
        _selectedTimeZone.value = TimeZone.getTimeZone(timeZoneId)
    }
}
