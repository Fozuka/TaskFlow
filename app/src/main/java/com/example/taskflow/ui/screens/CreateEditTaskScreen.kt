// File: ui/screens/CreateEditTaskScreen.kt
package com.example.taskflow.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.taskflow.model.Task
import com.example.taskflow.ui.components.CustomCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEditTaskScreen(
    task: Task? = null,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var title by remember(task) { mutableStateOf(task?.title ?: "") }
    var description by remember(task) { mutableStateOf(task?.description ?: "") }
    var category by remember(task) { mutableStateOf(task?.category ?: "Работа") }
    var priority by remember(task) { mutableStateOf(task?.priority?.capitalize() ?: "Medium") }
    var deadline by remember(task) { mutableStateOf<Date?>(task?.deadline) }
    var repeat by remember(task) { mutableStateOf(task?.repeat ?: false) }
    var repeatFrequency by remember(task) { mutableStateOf(task?.repeatFrequency ?: "daily") }

    val categories = listOf("Работа", "Учёба", "Личное", "Другое")
    val priorities = listOf("Low", "Medium", "High")
    val frequencies = mapOf("daily" to "Ежедневно", "weekly" to "Еженедельно")

    // Форматирование дедлайна
    val deadlineText by remember(deadline) {
        derivedStateOf {
            deadline?.let {
                SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(it)
            } ?: "Не установлен"
        }
    }

    // Диалог выбора даты и времени
    var showDateTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task == null) "Создать задачу" else "Редактировать задачу") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "Введите название", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }
                val newTask = task?.copy(
                    title = title,
                    description = if (description.isBlank()) null else description,
                    category = category,
                    priority = priority.lowercase(),
                    deadline = deadline,
                    repeat = repeat,
                    repeatFrequency = repeatFrequency
                ) ?: Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = if (description.isBlank()) null else description,
                    category = category,
                    priority = priority.lowercase(),
                    deadline = deadline,
                    completed = false,
                    repeat = repeat,
                    repeatFrequency = repeatFrequency
                )
                onSave(newTask)
            }) {
                Icon(Icons.Default.Save, "Сохранить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            CustomCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Категория", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        categories.forEach {
                            FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it) })
                        }
                    }
                }
            }

            CustomCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Приоритет", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        priorities.forEach { prio ->
                            FilterChip(
                                selected = priority == prio,
                                onClick = { priority = prio },
                                label = { Text(prio) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (prio) {
                                        "High" -> Color(0xFFB00020)
                                        "Medium" -> Color(0xFFFFB300)
                                        "Low" -> Color(0xFF66BB6A)
                                        else -> MaterialTheme.colorScheme.secondary
                                    },
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // ДЕДЛАЙН — ТОЧНО КАК В ANALYTICS (воскресенье видно!)
            CustomCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Дедлайн", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showDateTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("$deadlineText")
                    }
                }
            }

            if (showDateTimePicker) {
                DateTimePicker(
                    initialDate = deadline,
                    onDismiss = { showDateTimePicker = false },
                    onSelect = { date ->
                        deadline = date
                        showDateTimePicker = false
                    }
                )
            }

            // Повторение
            CustomCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Повторять", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Switch(checked = repeat, onCheckedChange = { repeat = it })
                    }
                    if (repeat) {
                        Spacer(Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            frequencies.forEach { (key, label) ->
                                FilterChip(selected = repeatFrequency == key, onClick = { repeatFrequency = key }, label = { Text(label) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    initialDate: Date?,
    onDismiss: () -> Unit,
    onSelect: (Date) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.time ?: System.currentTimeMillis(),
        // Важно: включаем отображение названий дней недели
        initialDisplayMode = DisplayMode.Picker
    )

    if (!showTime) {
        // Вот ключевое изменение — как в аналитике
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            selectedDate = Date(millis)
                            showTime = true
                        }
                    }
                ) {
                    Text("Далее")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            },
            // Это заставляет диалог быть широким — как в Analytics
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // Оборачиваем в Column с padding, чтобы было красиво
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    // Опционально: можно убрать заголовок, если он мешает
                    showModeToggle = true
                )
            }
        }
    } else {
        // Время — оставляем как было
        val calendar = Calendar.getInstance().apply {
            timeInMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
        }
        val timeState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Выберите время") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    val finalCalendar = Calendar.getInstance().apply {
                        timeInMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, timeState.hour)
                        set(Calendar.MINUTE, timeState.minute)
                    }
                    onSelect(finalCalendar.time)
                }) {
                    Text("Готово")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) {
                    Text("Назад")
                }
            }
        )
    }
}