package com.devomar.clock.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devomar.clock.ClockApplication
import com.devomar.clock.R

object TimerNotifier {

    fun notify(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (nm.areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(context, ClockApplication.TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(context.getString(R.string.notif_timer_title))
                .setContentText(context.getString(R.string.notif_timer_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
        }
    }

    private const val NOTIF_ID = 2001
}
