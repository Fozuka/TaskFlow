// File: ui/screens/SettingsScreen.kt
package com.example.taskflow.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.SettingsPreferences
import com.example.taskflow.ui.components.CustomCard
import com.example.taskflow.ui.components.CustomDialog
import com.example.taskflow.utils.NotificationScheduler
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsPreferences: SettingsPreferences,
    onExport: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Читаем текущие настройки
    val isDark by settingsPreferences.isDarkTheme.collectAsState(initial = false)
    val notificationsEnabled by settingsPreferences.notificationsEnabled.collectAsState(initial = true)
    val reminderTime by settingsPreferences.dailyReminderTime.collectAsState(initial = "09:00")
    val language by settingsPreferences.language.collectAsState(initial = "ru")

    var showExportDialog by remember { mutableStateOf(false) }
    var showDangerZoneDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Настройки") })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Внешний вид
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Внешний вид", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Тёмная тема")
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch { settingsPreferences.setDarkTheme(enabled) }
                            }
                        )
                    }
                }
            }

            // Уведомления
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Уведомления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    // Время утреннего напоминания
                    ListItem(
                        headlineContent = { Text("Время утреннего напоминания") },
                        supportingContent = { Text(reminderTime) },
                        leadingContent = { Icon(Icons.Default.Schedule, null) },
                        modifier = Modifier.clickable { showTimePicker = true }
                    )
                    HorizontalDivider()

                    // Напоминания о задачах
                    ListItem(
                        headlineContent = { Text("Напоминания о задачах") },
                        supportingContent = { Text("Получать уведомления о дедлайнах") },
                        trailingContent = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    coroutineScope.launch { settingsPreferences.setNotificationsEnabled(enabled) }
                                }
                            )
                        }
                    )
                }
            }

            // Язык
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Язык", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (language == "ru") "Русский" else "English",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Русский") },
                                onClick = {
                                    coroutineScope.launch { settingsPreferences.setLanguage("ru") }
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = {
                                    coroutineScope.launch { settingsPreferences.setLanguage("en") }
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Экспорт данных
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Данные", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Экспортируйте все задачи в CSV-файл для резервного копирования.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.ImportExport, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Экспортировать данные")
                    }
                }
            }

            // Опасная зона
            CustomCard{
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Опасная зона",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB00020)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Удаление данных необратимо.", color = Color(0xFFB00020))
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { showDangerZoneDialog = true },
                        border = BorderStroke(1.dp, Color(0xFFB00020)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB00020)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Сбросить все данные")
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    // Диалоги
    if (showExportDialog) {
        CustomDialog(
            onDismissRequest = { showExportDialog = false },
            title = "Экспорт данных",
            description = "Файл будет сохранён в папку Загрузки.",
            buttons = {
                TextButton(onClick = { showExportDialog = false }) { Text("Отмена") }
                Button(onClick = { showExportDialog = false; onExport() }) { Text("Экспорт") }
            }
        )
    }

    if (showDangerZoneDialog) {
        CustomDialog(
            onDismissRequest = { showDangerZoneDialog = false },
            title = "Удалить все задачи?",
            description = "Это действие нельзя отменить.",
            buttons = {
                TextButton(onClick = { showDangerZoneDialog = false }) { Text("Отмена") }
                Button(
                    onClick = { showDangerZoneDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB00020))
                ) { Text("Удалить") }
            }
        )
    }

    // TimePicker для выбора времени напоминания
    if (showTimePicker) {
        val context = LocalContext.current
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                coroutineScope.launch {
                    settingsPreferences.setDailyReminderTime(time)
                    // Просто перезапланируем — задачи возьмутся при старте приложения
                    NotificationScheduler.scheduleDailyReminder(context, reminderTime)
                }
                showTimePicker = false
            }
        )
    }
}

// Простой TimePickerDialog (добавь в отдельный файл или сюда)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val timeState = rememberTimePickerState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Время напоминания") },
        text = {
            TimePicker(state = timeState)
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timeState.hour, timeState.minute)
                onDismiss()
            }) { Text("ОК") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}