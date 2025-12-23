package com.example.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.taskflow.model.Task
import com.example.taskflow.ui.components.*
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    tasks: List<Task>,
    onExport: () -> Unit,
    onNavigate: (String) -> Unit
) {
    // Состояния для выбора периода
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedFromDate by remember { mutableStateOf<Date?>(null) }
    var selectedToDate by remember { mutableStateOf<Date?>(null) }

    // Состояния для раскрытия карточек
    var categoriesExpanded by remember { mutableStateOf(true) }
    var priorityExpanded by remember { mutableStateOf(false) }

    // Фильтрация задач по выбранному периоду
    val filteredTasks = remember(tasks, selectedFromDate, selectedToDate) {
        tasks.filter { task ->
            val deadlineTime = task.deadline?.time ?: return@filter false
            val from = selectedFromDate?.time ?: Long.MIN_VALUE
            val to = selectedToDate?.time ?: Long.MAX_VALUE
            deadlineTime in from..to
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Аналитика") }
                // Стрелка «Назад» убрана — переключение только через bottom bar
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Описание
            CustomCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.ImportExport,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Аналитика показывает статистику по вашим задачам за выбранный период.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Выбор периода
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Период", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            when {
                                selectedFromDate != null && selectedToDate != null ->
                                    "${formatShortDate(selectedFromDate!!)} – ${formatShortDate(selectedToDate!!)}"
                                else -> "Выберите период"
                            }
                        )
                    }
                }
            }

            // Основная статистика
            CustomCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Статистика", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    val total = filteredTasks.size
                    val completed = filteredTasks.count { it.completed }
                    val inProgress = total - completed

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("Всего", total, MaterialTheme.colorScheme.primary)
                        StatItem("Выполнено", completed, Color(0xFF66BB6A))
                        StatItem("В работе", inProgress, Color(0xFFFFB300))
                    }
                }
            }

            // Задачи по категориям — РАБОТАЕТ!
            ExpandableCard(
                title = "Задачи по категориям",
                expanded = categoriesExpanded,
                onExpandedChange = { categoriesExpanded = it }
            ) {
                val grouped = filteredTasks.groupBy { it.category }
                if (grouped.isEmpty()) {
                    Text(
                        "Нет данных",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    grouped.forEach { (category, list) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category, style = MaterialTheme.typography.bodyMedium)
                            Badge(text = list.size.toString(), variant = BadgeVariant.Outline)
                        }
                    }
                }
            }

            // Задачи по приоритету — РАБОТАЕТ!
            ExpandableCard(
                title = "Задачи по приоритету",
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = it }
            ) {
                val grouped = filteredTasks.groupBy { it.priority.lowercase() }
                val labels = mapOf("high" to "Высокий", "medium" to "Средний", "low" to "Низкий")

                listOf("high", "medium", "low").forEach { key ->
                    val count = grouped[key]?.size ?: 0
                    if (count > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(labels[key]!!, style = MaterialTheme.typography.bodyMedium)
                            Badge(text = count.toString(), variant = BadgeVariant.Outline)
                        }
                    }
                }

                if (grouped.isEmpty()) {
                    Text(
                        "Нет данных",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Кнопка экспорта
            Button(
                onClick = onExport,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Outlined.ImportExport, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Экспортировать в CSV")
            }

            Spacer(Modifier.height(80.dp)) // отступ под bottom bar
        }
    }

    // Диалог выбора периода
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Выберите период") },
            text = {
                DateRangePicker(
                    onSelect = { from, to ->
                        selectedFromDate = Date.from(
                            from.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        )
                        selectedToDate = Date.from(
                            to.atTime(23, 59, 59, 999_999_999)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        )
                        showDatePicker = false
                    },
                    onClose = { showDatePicker = false }
                )
            },
            confirmButton = {},
            dismissButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        )
    }
}

// Вспомогательные компоненты
@Composable
private fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

private fun formatShortDate(date: Date): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM")
    return java.time.Instant.ofEpochMilli(date.time)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}