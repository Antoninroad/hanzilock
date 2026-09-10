package com.antoninclouet.hanzilock.data

import android.content.Context

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Réglages d'affichage et de rappel (séparés de la progression). */
class AppSettings private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("hanzi_settings", Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() = runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, null) ?: "") }
            .getOrDefault(ThemeMode.SYSTEM)
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER, value).apply()

    /** Notification permanente = caractère du jour visible sur l'écran verrouillé. */
    var lockScreenCard: Boolean
        get() = prefs.getBoolean(KEY_LOCK_CARD, false)
        set(value) = prefs.edit().putBoolean(KEY_LOCK_CARD, value).apply()

    /** Heure du rappel, encodée hh*60+mm. Défaut 09:00. */
    var reminderMinutes: Int
        get() = prefs.getInt(KEY_REMINDER_MIN, 9 * 60)
        set(value) = prefs.edit().putInt(KEY_REMINDER_MIN, value).apply()

    val reminderHour get() = reminderMinutes / 60
    val reminderMinute get() = reminderMinutes % 60

    companion object {
        private const val KEY_THEME = "theme"
        private const val KEY_REMINDER = "reminder"
        private const val KEY_REMINDER_MIN = "reminderMinutes"
        private const val KEY_LOCK_CARD = "lockScreenCard"

        @Volatile
        private var instance: AppSettings? = null

        fun get(context: Context): AppSettings =
            instance ?: synchronized(this) {
                instance ?: AppSettings(context.applicationContext).also { instance = it }
            }
    }
}
