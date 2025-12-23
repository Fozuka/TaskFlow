// File: model/Task.kt
package com.example.taskflow.model

import java.util.Date

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val category: String,
    val priority: String,
    val deadline: Date?,
    val completed: Boolean,
    val repeat: Boolean = false,
    val repeatFrequency: String = "daily",
    val createdAt: Date = Date(),
    val completedAt: Date? = null
)
