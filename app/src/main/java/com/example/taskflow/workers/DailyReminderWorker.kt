// File: workers/DailyReminderWorker.kt
package com.example.taskflow.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.taskflow.R
import com.example.taskflow.data.TaskRepository
import com.example.taskflow.data.local.AppDatabase

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "daily_reminder"
    }

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = TaskRepository(db.taskDao())

        val count = repository.countTasksForToday()

        if (count > 0) {
            sendNotification(count)
        }

        return Result.success()
    }

    private fun sendNotification(count: Int) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ежедневные напоминания",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val word = when {
            count % 10 == 1 && count % 100 != 11 -> "задача"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "задачи"
            else -> "задач"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name) // или ic_launcher
            .setContentTitle("У тебя $count $word на сегодня!")
            .setContentText("Открой TaskFlow и выполни их")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
