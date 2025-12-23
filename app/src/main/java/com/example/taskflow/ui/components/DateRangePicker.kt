// File: ui/components/DateRangePicker.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskflow.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    onSelect: (LocalDate, LocalDate) -> Unit,
    onClose: () -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    // Состояния для DatePicker
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Выберите период",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Начало"
                )
            }

            Text(text = "—")

            OutlinedButton(
                onClick = { showEndDatePicker = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = endDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "Конец"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onClose) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    if (startDate != null && endDate != null) {
                        onSelect(startDate!!, endDate!!)
                        onClose()
                    }
                },
                enabled = startDate != null && endDate != null
            ) {
                Text("Применить")
            }
        }
    }

    // Диалог выбора даты начала
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selected = startDatePickerState.selectedDateMillis
                        if (selected != null) {
                            startDate = java.time.Instant.ofEpochMilli(selected)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = startDatePickerState
            )
        }
    }

    // Диалог выбора даты окончания
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        val selected = endDatePickerState.selectedDateMillis
                        if (selected != null) {
                            endDate = java.time.Instant.ofEpochMilli(selected)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState
            )
        }
    }
}
