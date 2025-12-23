package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val dashboardViewModel: DashboardViewModel,
    private val taskId: String
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            // Имитация задержки
            kotlinx.coroutines.delay(300)

            val loadedTask = dashboardViewModel.tasks.value.find { it.id == taskId }
            _task.value = loadedTask
            _isLoading.update { false }
        }
    }

    fun toggleComplete() {
        _task.value?.let { currentTask ->
            val updatedTask = currentTask.copy(
                completed = !currentTask.completed,
                completedAt = if (currentTask.completed) null else Date()
            )
            _task.value = updatedTask

            // Обновляем в DashboardViewModel
            dashboardViewModel.toggleComplete(taskId)
        }
    }

    fun deleteTask() {
        _task.value?.let {
            // Удаляем из DashboardViewModel (условно)
            // В реальности — через репозиторий
            dashboardViewModel.deleteTask(taskId) // ← Нужно добавить в DashboardViewModel
        }
    }

    fun editTask() {
        // Пока заглушка — позже перейдём на экран редактирования
    }
}