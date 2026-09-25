package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val type: String, // TASK_EARNING, REFERRAL_COMMISSION, DAILY_BONUS, WITHDRAWAL, WELCOME_BONUS
    val amount: Double,
    val isCredit: Boolean, // true for earnings/credits, false for withdrawals
    val status: String, // COMPLETED, PENDING, PROCESSING, FAILED
    val reference: String,
    val timestamp: Long = System.currentTimeMillis()
)
