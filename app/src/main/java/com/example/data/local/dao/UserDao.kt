package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE refId = :refId LIMIT 1")
    suspend fun getUserByRefId(refId: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balance = balance + :amount, totalEarned = totalEarned + :amount WHERE id = :userId")
    suspend fun addEarnings(userId: Long, amount: Double)

    @Query("UPDATE users SET balance = balance - :amount, totalWithdrawn = totalWithdrawn + :amount WHERE id = :userId")
    suspend fun deductWithdrawal(userId: Long, amount: Double)

    @Query("UPDATE users SET paymentJazzCash = :jazzCash, paymentEasypaisa = :easypaisa, paymentBank = :bank, paymentCrypto = :crypto WHERE id = :userId")
    suspend fun updatePaymentMethods(userId: Long, jazzCash: String, easypaisa: String, bank: String, crypto: String)

    @Query("UPDATE users SET isKycVerified = 1 WHERE id = :userId")
    suspend fun verifyKyc(userId: Long)

    @Query("UPDATE users SET passwordHash = :newHash WHERE id = :userId")
    suspend fun updatePassword(userId: Long, newHash: String)

    @Query("UPDATE users SET securityPin = :pin WHERE id = :userId")
    suspend fun updateSecurityPin(userId: Long, pin: String)
}
