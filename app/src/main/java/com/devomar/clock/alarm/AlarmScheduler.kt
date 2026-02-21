package com.devomar.clock.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.devomar.clock.alarm.model.Alarm
import java.util.Calendar

object AlarmScheduler {

    const val ACTION_FIRE = "com.devomar.clock.ACTION_FIRE_ALARM"

    fun schedule(context: Context, alarm: Alarm) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE
            putExtra("alarm_id", alarm.id)
            putExtra("alarm_label", alarm.label)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = nextTriggerMillis(alarm)
        val alarmInfo = AlarmManager.AlarmClockInfo(triggerMillis, pi)
        am.setAlarmClock(alarmInfo, pi)
    }

    fun cancel(context: Context, alarm: Alarm) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_FIRE
        }
        val pi = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(pi)
    }

    private fun nextTriggerMillis(alarm: Alarm): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            if (alarm.repeatDays == 0) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            } else {
                // Find the next enabled day of week
                val today = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun ... 7=Sat
                for (i in 1..7) {
                    val dayIndex = (today - 1 + i) % 7  // 0=Sun ... 6=Sat
                    if (alarm.repeatDays and (1 shl dayIndex) != 0) {
                        cal.add(Calendar.DAY_OF_YEAR, i)
                        break
                    }
                }
            }
        } else if (alarm.repeatDays != 0) {
            val today = cal.get(Calendar.DAY_OF_WEEK) - 1  // 0=Sun ... 6=Sat
            if (alarm.repeatDays and (1 shl today) == 0) {
                for (i in 1..7) {
                    val dayIndex = (today + i) % 7
                    if (alarm.repeatDays and (1 shl dayIndex) != 0) {
                        cal.add(Calendar.DAY_OF_YEAR, i)
                        break
                    }
                }
            }
        }
        return cal.timeInMillis
    }
}
