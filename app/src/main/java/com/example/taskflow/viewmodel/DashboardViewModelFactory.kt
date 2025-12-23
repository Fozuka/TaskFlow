// File: viewmodel/DashboardViewModelFactory.kt
package com.example.taskflow.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskflow.data.TaskRepository
import com.example.taskflow.data.local.AppDatabase

class DashboardViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val repository: TaskRepository

    init {
        val db = AppDatabase.getInstance(context)
        repository = TaskRepository(db.taskDao())
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
