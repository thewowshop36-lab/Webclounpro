package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val referrerId: Long,
    val memberName: String,
    val memberEmail: String,
    val level: Int = 1, // 1 (15%), 2 (5%), 3 (2%)
    val commissionEarned: Double = 0.0,
    val joinedAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE"
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val amount: Double,
    val fee: Double,
    val netAmount: Double,
    val method: String, // JazzCash, Easypaisa, Bank Transfer, USDT (TRC20)
    val accountTitle: String,
    val accountNumber: String,
    val status: String = "PENDING", // PENDING, PROCESSING, COMPLETED, REJECTED
    val requestedAt: Long = System.currentTimeMillis(),
    val trxId: String = ""
)
