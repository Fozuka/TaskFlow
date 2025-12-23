package com.example.taskflow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.data.SettingsPreferences
import com.example.taskflow.ui.TaskFlowApp
import com.example.taskflow.ui.theme.TaskFlowTheme
import com.example.taskflow.utils.NotificationScheduler
import com.example.taskflow.viewmodel.DashboardViewModel
import com.example.taskflow.viewmodel.DashboardViewModelFactory

class MainActivity : ComponentActivity() {

    private val settingsPreferences by lazy { SettingsPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Читаем настройки
            val isDark by settingsPreferences.isDarkTheme.collectAsState(initial = false)
            val language by settingsPreferences.language.collectAsState(initial = "ru")
            val notificationsEnabled by settingsPreferences.notificationsEnabled.collectAsState(initial = true)
            val reminderTime by settingsPreferences.dailyReminderTime.collectAsState(initial = "09:00")

            // Локализованный контекст
            val context = LocalContext.current
            val localizedContext = remember(language) { context.applyLocale(language) }

            // ViewModel с фабрикой (чтобы был доступ к Room)
            val dashboardViewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(applicationContext)
            )

            CompositionLocalProvider(LocalContext provides localizedContext) {
                TaskFlowTheme(darkTheme = isDark) {

                    // Планируем/отменяем ежедневное напоминание
                    LaunchedEffect(notificationsEnabled, reminderTime) {
                        if (notificationsEnabled) {
                            NotificationScheduler.scheduleDailyReminder(
                                context = this@MainActivity,
                                time = reminderTime
                            )
                        } else {
                            NotificationScheduler.cancel(this@MainActivity)
                        }
                    }

                    // Запускаем приложение, пробрасываем настройки и ViewModel
                    TaskFlowApp(
                        settingsPreferences = settingsPreferences,
                        dashboardViewModel = dashboardViewModel
                    )
                }
            }
        }
    }
}

// Расширение для смены языка
fun Context.applyLocale(languageCode: String): Context {
    val locale = java.util.Locale(languageCode)
    java.util.Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}
