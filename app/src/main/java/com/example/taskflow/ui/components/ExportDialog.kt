package com.example.taskflow.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.taskflow.model.Task
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportDialog(
    tasks: List<Task>,
    onClose: () -> Unit,
    onExportSuccess: () -> Unit,
    onExportError: (String) -> Unit
) {
    val context = LocalContext.current
    var exportState by remember { mutableStateOf<ExportState>(ExportState.Idle) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        when {
            uri == null -> {
                exportState = ExportState.Error("Отменено пользователем")
            }
            isWritePermissionGranted(context) -> {
                exportState = ExportState.Loading
                exportTasksToCsv(context, tasks, uri) { success ->
                    if (success) {
                        exportState = ExportState.Success
                    } else {
                        exportState = ExportState.Error("Не удалось сохранить файл")
                    }
                }
            }
            else -> {
                exportState = ExportState.PermissionDenied
            }
        }
    }

    CustomDialog(
        onDismissRequest = onClose,
        title = "Экспорт задач",
        description = "Выберите место для сохранения CSV-файла с вашими задачами.",
        buttons = {
            TextButton(onClick = onClose) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    val filename = "tasks_export_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.csv"
                    exportLauncher.launch(filename)
                },
                enabled = exportState !is ExportState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Выбрать место")
            }
        }
    )

    if (exportState == ExportState.PermissionDenied) {
        PermissionDialog(
            onGrant = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
            },
            onDeny = { exportState = ExportState.Idle }
        )
    }

    if (exportState is ExportState.Success) {
        onExportSuccess()
        onClose()
    }

    if (exportState is ExportState.Error) {
        onExportError((exportState as ExportState.Error).message)
        onClose()
    }
}

@Composable
private fun PermissionDialog(
    onGrant: () -> Unit,
    onDeny: () -> Unit
) {
    CustomDialog(
        onDismissRequest = onDeny,
        title = "Требуется разрешение",
        description = "Разрешите приложению запись на устройство, чтобы сохранить файл. Перейдите в настройки и включите «Разрешить изменение данных».",
        buttons = {
            TextButton(onClick = onDeny) {
                Text("Позже")
            }
            Button(onClick = onGrant) {
                Text("Перейти в настройки")
            }
        }
    )
}

// Остальной код без изменений
private fun exportTasksToCsv(
    context: Context,
    tasks: List<Task>,
    uri: Uri,
    onResult: (Boolean) -> Unit
) {
    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
        val writer = OutputStreamWriter(outputStream, Charsets.UTF_8)
        try {
            writer.append("ID,Название,Описание,Категория,Приоритет,Дедлайн,Повторение,Создано,Выполнено,Дата выполнения\n")
            tasks.forEach { task ->
                writer.append(
                    listOf(
                        task.id,
                        escapeCsv(task.title),
                        escapeCsv(task.description ?: ""),
                        task.category,
                        task.priority,
                        task.deadline?.let { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it) } ?: "",
                        task.repeat.toString(),
                        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(task.createdAt),
                        task.completed.toString(),
                        task.completedAt?.let { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it) } ?: ""
                    ).joinToString(",") { "\"$it\"" }
                )
                writer.append("\n")
            }
            writer.flush()
            onResult(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(false)
        }
    } ?: run {
        onResult(false)
    }
}

private fun escapeCsv(value: String): String = value.replace("\"", "\"\"")

private fun isWritePermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    object Success : ExportState()
    object PermissionDenied : ExportState()
    data class Error(val message: String) : ExportState()
}