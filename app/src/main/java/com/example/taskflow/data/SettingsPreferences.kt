// File: data/SettingsPreferences.kt
package com.example.taskflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time") // "09:00"
        private val LANGUAGE = stringPreferencesKey("language") // "ru" или "en"
    }

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { it[IS_DARK_THEME] ?: false }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val dailyReminderTime: Flow<String> = dataStore.data.map { it[DAILY_REMINDER_TIME] ?: "09:00" }
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "ru" }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[IS_DARK_THEME] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setDailyReminderTime(time: String) {
        dataStore.edit { it[DAILY_REMINDER_TIME] = time }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[LANGUAGE] = lang }
    }
}