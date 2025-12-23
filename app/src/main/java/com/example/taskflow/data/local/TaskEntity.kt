// File: data/local/TaskEntity.kt
package com.example.taskflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val category: String,
    val priority: String,          // "high" | "medium" | "low"
    val deadlineMillis: Long?,     // null если нет срока
    val completed: Boolean,
    val repeat: Boolean,
    val repeatFrequency: String,   // "daily" | "weekly"
    val createdAtMillis: Long,
    val completedAtMillis: Long?
)
