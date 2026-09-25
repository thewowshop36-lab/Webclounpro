package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.TaskCompletionEntity
import com.example.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY id ASC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskEntity>)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordCompletion(completion: TaskCompletionEntity): Long

    @Query("SELECT * FROM task_completions WHERE userId = :userId ORDER BY completedAt DESC")
    fun getCompletionsForUser(userId: Long): Flow<List<TaskCompletionEntity>>

    @Query("SELECT COUNT(*) FROM task_completions WHERE userId = :userId AND taskId = :taskId AND completedAt >= :sinceTimestamp")
    suspend fun getCompletionCountToday(userId: Long, taskId: Long, sinceTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM task_completions WHERE userId = :userId AND completedAt >= :sinceTimestamp")
    suspend fun getTotalCompletedToday(userId: Long, sinceTimestamp: Long): Int
}
