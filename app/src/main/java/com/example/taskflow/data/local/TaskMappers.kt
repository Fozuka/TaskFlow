// File: data/local/TaskMappers.kt
package com.example.taskflow.data.local

import com.example.taskflow.model.Task
import java.util.Date

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    deadline = deadlineMillis?.let { Date(it) },
    completed = completed,
    repeat = repeat,
    repeatFrequency = repeatFrequency,
    createdAt = Date(createdAtMillis),
    completedAt = completedAtMillis?.let { Date(it) }
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    deadlineMillis = deadline?.time,
    completed = completed,
    repeat = repeat,
    repeatFrequency = repeatFrequency,
    createdAtMillis = createdAt.time,
    completedAtMillis = completedAt?.time
)
