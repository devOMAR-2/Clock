package com.devomar.clock.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var themeMode: Int
        get() = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit().putInt(KEY_THEME, value).apply()

    var languageCode: String
        get() = prefs.getString(KEY_LANGUAGE, LANG_EN) ?: LANG_EN
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var selectedTimezoneId: String
        get() = prefs.getString(KEY_TIMEZONE, DEFAULT_TIMEZONE) ?: DEFAULT_TIMEZONE
        set(value) = prefs.edit().putString(KEY_TIMEZONE, value).apply()

    companion object {
        private const val PREFS_NAME = "clock_prefs"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_LANGUAGE = "language_code"
        private const val KEY_TIMEZONE = "timezone_id"
        const val LANG_EN = "en"
        const val LANG_AR = "ar"
        private val DEFAULT_TIMEZONE = java.util.TimeZone.getDefault().id
    }
}
