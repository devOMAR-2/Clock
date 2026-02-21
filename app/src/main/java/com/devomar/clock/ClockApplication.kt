package com.devomar.clock

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.devomar.clock.alarm.AlarmService
import com.devomar.clock.util.LocaleHelper
import com.devomar.clock.util.PrefsManager

class ClockApplication : Application() {

    lateinit var prefs: PrefsManager
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)

        // Apply persisted theme
        AppCompatDelegate.setDefaultNightMode(prefs.themeMode)

        // Create notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels()
        }
    }

    override fun attachBaseContext(base: Context) {
        val prefs = PrefsManager(base)
        super.attachBaseContext(LocaleHelper.wrap(base, prefs.languageCode))
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmChannel = NotificationChannel(
            AlarmService.CHANNEL_ID,
            getString(R.string.channel_alarm_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_alarm_desc)
            setBypassDnd(true)
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val timerChannel = NotificationChannel(
            TIMER_CHANNEL_ID,
            getString(R.string.channel_timer_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.channel_timer_desc)
        }

        nm.createNotificationChannels(listOf(alarmChannel, timerChannel))
    }

    companion object {
        const val TIMER_CHANNEL_ID = "timer_channel"
    }
}
