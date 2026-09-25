package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val refId: String, // Unique user referral code
    val referredBy: String, // Sponsor referral code (e.g. Njk4MDc4)
    val balance: Double = 0.0,
    val pendingBalance: Double = 0.0,
    val totalEarned: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val tier: String = "Silver", // Silver, Gold, VIP Platinum
    val isKycVerified: Boolean = false,
    val securityPin: String = "",
    val paymentJazzCash: String = "",
    val paymentEasypaisa: String = "",
    val paymentBank: String = "",
    val paymentCrypto: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
