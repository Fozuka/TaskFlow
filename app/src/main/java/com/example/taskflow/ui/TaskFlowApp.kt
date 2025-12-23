// File: ui/TaskFlowApp.kt
package com.example.taskflow.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.taskflow.data.SettingsPreferences
import com.example.taskflow.ui.components.ExportDialog
import com.example.taskflow.ui.navigation.BottomNavigation
import com.example.taskflow.ui.screens.AnalyticsScreen
import com.example.taskflow.ui.screens.CreateEditTaskScreen
import com.example.taskflow.ui.screens.DashboardScreen
import com.example.taskflow.ui.screens.SettingsScreen
import com.example.taskflow.ui.screens.SplashScreen
import com.example.taskflow.ui.screens.TaskDetailScreen
import com.example.taskflow.viewmodel.DashboardViewModel

@Composable
fun TaskFlowApp(
    settingsPreferences: SettingsPreferences,
    dashboardViewModel: DashboardViewModel    // <-- получаем из Activity
) {
    val navController = rememberNavController()

    val tasks by dashboardViewModel.tasks.collectAsStateWithLifecycle()
    val loading by dashboardViewModel.isLoading.collectAsStateWithLifecycle()

    // Состояния для экспорта
    var showExportDialog by remember { mutableStateOf(false) }
    var exportSuccess by remember { mutableStateOf(false) }
    var exportError by remember { mutableStateOf<String?>(null) }

    // Текущий маршрут для BottomNavigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            when (currentRoute) {
                "dashboard", "analytics", "settings" -> {
                    BottomNavigation(
                        currentScreen = currentRoute ?: "dashboard",
                        onNavigate = { screen ->
                            navController.navigate(screen) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onTimeout = {
                    navController.navigate("dashboard") {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                })
            }

            composable("dashboard") {
                DashboardScreen(
                    tasks = tasks,
                    loading = loading,
                    error = null,
                    onNavigate = { navController.navigate(it) },
                    onToggleComplete = { dashboardViewModel.toggleComplete(it) },
                    onRefresh = { dashboardViewModel.refresh() }
                )
            }

            composable("create-task") {
                CreateEditTaskScreen(
                    onSave = { task ->
                        dashboardViewModel.addTask(task)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "task-detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("id")
                val task = taskId?.let { id -> tasks.find { it.id == id } }

                if (task != null) {
                    TaskDetailScreen(
                        task = task,
                        onEdit = { navController.navigate("edit-task/$taskId") },
                        onDelete = {
                            dashboardViewModel.deleteTask(task.id)
                            navController.popBackStack()
                        },
                        onToggleComplete = { dashboardViewModel.toggleComplete(task.id) },
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Задача не найдена")
                    }
                }
            }

            composable(
                route = "edit-task/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("id")
                val task = taskId?.let { id -> tasks.find { it.id == id } }

                if (task != null) {
                    CreateEditTaskScreen(
                        task = task,
                        onSave = { updatedTask ->
                            dashboardViewModel.updateTask(updatedTask)
                            navController.popBackStack()
                        },
                        onCancel = { navController.popBackStack() }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("Задача не найдена")
                    }
                }
            }

            composable("analytics") {
                AnalyticsScreen(
                    tasks = tasks,
                    onExport = { showExportDialog = true },
                    onNavigate = { navController.navigate(it) }
                )
            }

            composable("settings") {
                SettingsScreen(
                    settingsPreferences = settingsPreferences,
                    onExport = { showExportDialog = true }
                )
            }
        }

        // Диалоги экспорта
        if (showExportDialog) {
            ExportDialog(
                tasks = tasks,
                onClose = { showExportDialog = false },
                onExportSuccess = {
                    exportSuccess = true
                    showExportDialog = false
                },
                onExportError = { error ->
                    exportError = error
                    showExportDialog = false
                }
            )
        }

        if (exportSuccess) {
            AlertDialog(
                onDismissRequest = { exportSuccess = false },
                title = { Text("Успешно") },
                text = { Text("Данные экспортированы в CSV") },
                confirmButton = {
                    Button(onClick = { exportSuccess = false }) { Text("ОК") }
                }
            )
        }

        if (exportError != null) {
            AlertDialog(
                onDismissRequest = { exportError = null },
                title = { Text("Ошибка") },
                text = { Text(exportError!!) },
                confirmButton = {
                    Button(onClick = { exportError = null }) { Text("ОК") }
                }
            )
        }
    }
}
