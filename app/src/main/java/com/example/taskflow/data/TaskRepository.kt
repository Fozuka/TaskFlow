// File: data/TaskRepository.kt
package com.example.taskflow.data

import com.example.taskflow.data.local.TaskDao
import com.example.taskflow.data.local.toDomain
import com.example.taskflow.data.local.toEntity
import com.example.taskflow.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(
    private val taskDao: TaskDao
) {

    val tasksFlow: Flow<List<Task>> =
        taskDao.getAllTasksFlow().map { list -> list.map { it.toDomain() } }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun deleteTaskById(id: String) {
        taskDao.getById(id)?.let { taskDao.deleteTask(it) }
    }

    suspend fun toggleComplete(task: Task) {
        val updated = task.copy(
            completed = !task.completed,
            completedAt = if (task.completed) null else java.util.Date()
        )
        taskDao.updateTask(updated.toEntity())
    }

    suspend fun deleteAll() {
        taskDao.deleteAll()
    }

    suspend fun countTasksForToday(): Int {
        return taskDao.countTasksForToday()
    }
}
