// File: viewmodel/DashboardViewModel.kt
package com.example.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.TaskRepository
import com.example.taskflow.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            _isLoading.value = true
            repository.tasksFlow.collect { list ->
                _tasks.value = list
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        // Room сам даёт актуальные данные через Flow.
        // Если хочешь — можешь тут ничего не делать.
    }

    fun toggleComplete(taskId: String) {
        viewModelScope.launch {
            val task = _tasks.value.find { it.id == taskId } ?: return@launch
            repository.toggleComplete(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
