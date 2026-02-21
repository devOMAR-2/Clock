package com.devomar.clock.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devomar.clock.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) return

        CoroutineScope(Dispatchers.IO).launch {
            val alarms = AppDatabase.getInstance(context).alarmDao().getEnabledAlarms()
            alarms.forEach { AlarmScheduler.schedule(context, it) }
        }
    }
}
