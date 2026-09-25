package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // WATCH_AD, PTC_CLICK, VIDEO_REVIEW, DAILY_BONUS
    val rewardAmount: Double,
    val durationSeconds: Int,
    val actionUrl: String = "",
    val sponsorName: String = "Denvork Sponsor",
    val maxPerDay: Int = 10,
    val isDaily: Boolean = true
)

@Entity(tableName = "task_completions")
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val taskId: Long,
    val earnedAmount: Double,
    val completedAt: Long = System.currentTimeMillis(),
    val notesOrProof: String = ""
)
