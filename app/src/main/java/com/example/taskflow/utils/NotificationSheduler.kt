package com.example.taskflow.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskflow.workers.DailyReminderWorker
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val DAILY_WORK_NAME = "daily_reminder"

    fun scheduleDailyReminder(context: Context, time: String = "09:00") {
        val (hour, minute) = time.split(":").map { it.toInt() }

        val now = LocalDateTime.now()
        var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (target.isBefore(now)) target = target.plusDays(1)

        val delayMs = java.time.Duration.between(now, target).toMillis()

        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_WORK_NAME)
    }
}
