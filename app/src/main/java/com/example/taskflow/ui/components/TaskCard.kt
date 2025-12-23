// File: ui/components/TaskCard.kt
package com.example.taskflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.taskflow.model.Task
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: (String) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (task.completed) 0.6f else 1.0f)
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onToggleComplete(task.id) },
                modifier = Modifier.padding(top = 4.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6200EE),
                    checkmarkColor = Color.White,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Основное содержимое
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
            ) {
                // Заголовок + приоритет
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.completed)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1
                    )

                    // Иконка приоритета
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Приоритет",
                        tint = getPriorityColor(task.priority),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Категория + дедлайн
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Категория
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getCategoryColor(task.category),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Дедлайн
                    if (task.deadline != null) {
                        val formattedDeadline = formatDeadline(task.deadline)
                        val isOverdue = !task.completed && task.deadline.before(java.util.Date())

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Дедлайн",
                                tint = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = formattedDeadline,
                                color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getCategoryColor(category: String): Color = when (category) {
    "Работа" -> Color(0xFF6200EE)
    "Учёба" -> Color(0xFF03DAC6)
    "Личное" -> Color(0xFFFF6B6B)
    "Другое" -> Color(0xFF95A5A6)
    else -> Color(0xFF95A5A6)
}

@Composable
fun getPriorityColor(priority: String): Color = when (priority.lowercase()) {
    "high" -> Color(0xFFB00020)    // красный
    "medium" -> Color(0xFFFFB300)  // жёлтый/оранжевый
    "low" -> Color(0xFF66BB6A)     // зелёный
    else -> Color(0xFF90A4AE)      // серый
}

fun formatDeadline(deadline: java.util.Date): String {
    val zoneId = ZoneId.systemDefault()
    val now = LocalDateTime.now()
    val deadlineLdt = LocalDateTime.ofInstant(deadline.toInstant(), zoneId)
    val today = now.toLocalDate()
    val deadlineDate = deadlineLdt.toLocalDate()

    return when {
        deadlineDate == today -> "Сегодня, ${String.format("%02d:%02d", deadlineLdt.hour, deadlineLdt.minute)}"
        deadlineDate == today.plusDays(1) -> "Завтра, ${String.format("%02d:%02d", deadlineLdt.hour, deadlineLdt.minute)}"
        else -> "${String.format("%02d.%02d", deadlineDate.dayOfMonth, deadlineDate.monthValue)}, ${String.format("%02d:%02d", deadlineLdt.hour, deadlineLdt.minute)}"
    }
}
