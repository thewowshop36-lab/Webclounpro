package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ReferralDao
import com.example.data.local.dao.TaskDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WithdrawalDao
import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TaskCompletionEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        TaskCompletionEntity::class,
        TransactionEntity::class,
        ReferralEntity::class,
        WithdrawalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DenvorkDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun transactionDao(): TransactionDao
    abstract fun referralDao(): ReferralDao
    abstract fun withdrawalDao(): WithdrawalDao

    companion object {
        @Volatile
        private var INSTANCE: DenvorkDatabase? = null

        fun getDatabase(context: Context): DenvorkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DenvorkDatabase::class.java,
                    "denvork_portal_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
