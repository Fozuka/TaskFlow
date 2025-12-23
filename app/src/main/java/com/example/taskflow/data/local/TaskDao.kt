// File: data/local/TaskDao.kt
package com.example.taskflow.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TaskEntity?

    @Query(
        "SELECT COUNT(*) FROM tasks " +
                "WHERE completed = 0 " +
                "AND deadlineMillis IS NOT NULL " +
                "AND date(deadlineMillis / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')"
    )
    suspend fun countTasksForToday(): Int
}
