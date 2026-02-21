package com.devomar.clock.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmScheduler.ACTION_FIRE -> {
                val alarmId = intent.getIntExtra("alarm_id", -1)
                val label = intent.getStringExtra("alarm_label") ?: ""
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("alarm_id", alarmId)
                    putExtra("alarm_label", label)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
            ACTION_DISMISS -> {
                context.stopService(Intent(context, AlarmService::class.java))
            }
        }
    }

    companion object {
        const val ACTION_DISMISS = "com.devomar.clock.ACTION_DISMISS_ALARM"
    }
}
