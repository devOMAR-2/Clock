package com.devomar.clock.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val isEnabled: Boolean = true,
    /** Bitmask: bit 0 = Sun, bit 1 = Mon, ..., bit 6 = Sat. 0 = one-time. */
    val repeatDays: Int = 0
)
