package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE userId = :userId AND isCredit = 1 AND timestamp >= :sinceTimestamp")
    suspend fun getTodayEarnings(userId: Long, sinceTimestamp: Long): Double
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals WHERE referrerId = :userId ORDER BY joinedAt DESC")
    fun getReferralsForUser(userId: Long): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReferrals(referrals: List<ReferralEntity>)

    @Query("SELECT COUNT(*) FROM referrals WHERE referrerId = :userId")
    suspend fun getReferralCount(userId: Long): Int

    @Query("SELECT COALESCE(SUM(commissionEarned), 0.0) FROM referrals WHERE referrerId = :userId")
    suspend fun getTotalReferralEarnings(userId: Long): Double
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getWithdrawalsForUser(userId: Long): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Query("UPDATE withdrawals SET status = :status, trxId = :trxId WHERE id = :id")
    suspend fun updateWithdrawalStatus(id: Long, status: String, trxId: String)
}
