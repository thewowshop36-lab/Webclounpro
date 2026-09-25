package com.example.data.repository

import com.example.data.local.DenvorkDatabase
import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TaskCompletionEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Calendar
import java.util.UUID

class DenvorkRepository(private val db: DenvorkDatabase) {

    private val userDao = db.userDao()
    private val taskDao = db.taskDao()
    private val transactionDao = db.transactionDao()
    private val referralDao = db.referralDao()
    private val withdrawalDao = db.withdrawalDao()

    fun getUser(userId: Long): Flow<UserEntity?> = userDao.getUserById(userId)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTransactions(userId: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsForUser(userId)

    fun getReferrals(userId: Long): Flow<List<ReferralEntity>> = referralDao.getReferralsForUser(userId)

    fun getWithdrawals(userId: Long): Flow<List<WithdrawalEntity>> = withdrawalDao.getWithdrawalsForUser(userId)

    fun getCompletions(userId: Long): Flow<List<TaskCompletionEntity>> = taskDao.getCompletionsForUser(userId)

    suspend fun initSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        // Check if sponsor user with ref_id=Njk4MDc4 exists
        val sponsor = userDao.getUserByRefId("Njk4MDc4")
        if (sponsor == null) {
            val sponsorUser = UserEntity(
                fullName = "Rabnawaz (Verified Sponsor)",
                email = "rabnawaz.sponsor@denvork.com",
                phone = "+92 300 1234567",
                passwordHash = hashPassword("denvork123"),
                refId = "Njk4MDc4",
                referredBy = "DIRECT",
                balance = 285.50,
                pendingBalance = 24.00,
                totalEarned = 1450.00,
                totalWithdrawn = 1140.50,
                tier = "VIP Platinum",
                isKycVerified = true,
                paymentJazzCash = "03001234567",
                paymentEasypaisa = "03001234567",
                paymentBank = "HBL - PK64HABB00001234567890",
                paymentCrypto = "TLd87Hskn29zM192KsknLa981"
            )
            val sponsorId = userDao.insertUser(sponsorUser)

            // Seed some referrals for sponsor
            referralDao.insertAllReferrals(
                listOf(
                    ReferralEntity(referrerId = sponsorId, memberName = "Ali Raza", memberEmail = "ali@mail.com", level = 1, commissionEarned = 14.50),
                    ReferralEntity(referrerId = sponsorId, memberName = "Hamza Tariq", memberEmail = "hamza@mail.com", level = 1, commissionEarned = 9.20),
                    ReferralEntity(referrerId = sponsorId, memberName = "Usman Ghani", memberEmail = "usman@mail.com", level = 2, commissionEarned = 4.30),
                    ReferralEntity(referrerId = sponsorId, memberName = "Zain Malik", memberEmail = "zain@mail.com", level = 1, commissionEarned = 18.00)
                )
            )
        }

        // Seed tasks if empty
        if (taskDao.getTaskCount() == 0) {
            val defaultTasks = listOf(
                TaskEntity(
                    title = "Premium Video Ad #101",
                    description = "Watch sponsored partner high-yield promotional ad for 20 seconds.",
                    category = "WATCH_AD",
                    rewardAmount = 0.50,
                    durationSeconds = 20,
                    sponsorName = "Apex Global Trading",
                    maxPerDay = 8
                ),
                TaskEntity(
                    title = "Interactive Mobile Ad #102",
                    description = "Watch and interact with new fintech mobile application preview.",
                    category = "WATCH_AD",
                    rewardAmount = 0.40,
                    durationSeconds = 15,
                    sponsorName = "NexGen Pay",
                    maxPerDay = 10
                ),
                TaskEntity(
                    title = "Tech Sponsor Clip #103",
                    description = "Watch cloud infrastructure showcase clip and complete anti-bot check.",
                    category = "WATCH_AD",
                    rewardAmount = 0.65,
                    durationSeconds = 25,
                    sponsorName = "CloudForge AI",
                    maxPerDay = 6
                ),
                TaskEntity(
                    title = "Visit & Explore E-Commerce Partner",
                    description = "Click to visit e-commerce store page and stay for 30 seconds (PTC).",
                    category = "PTC_CLICK",
                    rewardAmount = 0.35,
                    durationSeconds = 30,
                    actionUrl = "https://example.com/partner-store",
                    sponsorName = "MegaMart Online"
                ),
                TaskEntity(
                    title = "Explore Crypto Gateway Portal",
                    description = "Paid-To-Click portal review. Browse the homepage and verify interaction.",
                    category = "PTC_CLICK",
                    rewardAmount = 0.45,
                    durationSeconds = 25,
                    actionUrl = "https://example.com/crypto-portal",
                    sponsorName = "BitShield Network"
                ),
                TaskEntity(
                    title = "Upload YouTube / TikTok Review Video",
                    description = "Create a genuine 1-2 min video reviewing Denvork Portal with your referral link and submit the video URL for approval.",
                    category = "VIDEO_REVIEW",
                    rewardAmount = 8.50,
                    durationSeconds = 60,
                    sponsorName = "Denvork Official Media",
                    maxPerDay = 1,
                    isDaily = false
                ),
                TaskEntity(
                    title = "Daily Attendance & Check-In",
                    description = "Claim your daily loyalty reward to maintain your active earning streak.",
                    category = "DAILY_BONUS",
                    rewardAmount = 0.25,
                    durationSeconds = 5,
                    sponsorName = "Denvork Rewards",
                    maxPerDay = 1
                )
            )
            taskDao.insertAllTasks(defaultTasks)
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        referredBy: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = userDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }

        // Generate clean unique referral ID (e.g. DV + 5 digits)
        val generatedRefId = "DV" + (10000 + (Math.random() * 89999).toInt()).toString()
        val cleanRef = if (referredBy.isBlank()) "Njk4MDc4" else referredBy.trim()

        val newUser = UserEntity(
            fullName = fullName.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            passwordHash = hashPassword(password),
            refId = generatedRefId,
            referredBy = cleanRef,
            balance = 2.00, // $2.00 Welcome Sign Up Bonus!
            totalEarned = 2.00,
            tier = "Silver"
        )

        val newId = userDao.insertUser(newUser)

        // Log welcome bonus transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = newId,
                title = "Welcome Registration Bonus",
                type = "WELCOME_BONUS",
                amount = 2.00,
                isCredit = true,
                status = "COMPLETED",
                reference = "BONUS-REG-${System.currentTimeMillis().toString().takeLast(6)}"
            )
        )

        // If referred by someone, record referral record and give sponsor reward
        val sponsor = userDao.getUserByRefId(cleanRef)
        if (sponsor != null) {
            referralDao.insertReferral(
                ReferralEntity(
                    referrerId = sponsor.id,
                    memberName = fullName.trim(),
                    memberEmail = cleanEmail,
                    level = 1,
                    commissionEarned = 1.00
                )
            )
            // Add $1.00 referral bonus to sponsor
            userDao.addEarnings(sponsor.id, 1.00)
            transactionDao.insertTransaction(
                TransactionEntity(
                    userId = sponsor.id,
                    title = "Referral Bonus: $fullName joined",
                    type = "REFERRAL_COMMISSION",
                    amount = 1.00,
                    isCredit = true,
                    status = "COMPLETED",
                    reference = "REF-BONUS-${System.currentTimeMillis().toString().takeLast(6)}"
                )
            )
        }

        val created = userDao.getUserByIdSync(newId)
        if (created != null) {
            Result.success(created)
        } else {
            Result.failure(Exception("Failed to create user account."))
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail)
            ?: return@withContext Result.failure(Exception("User not found with this email."))

        if (user.passwordHash != hashPassword(password)) {
            return@withContext Result.failure(Exception("Incorrect password. Please try again."))
        }

        Result.success(user)
    }

    suspend fun completeTask(
        userId: Long,
        taskId: Long,
        taskTitle: String,
        category: String,
        reward: Double,
        proofNotes: String = ""
    ): Result<Double> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))

        // Check if daily limit reached
        val startOfToday = getStartOfTodayMillis()
        val countToday = taskDao.getCompletionCountToday(userId, taskId, startOfToday)
        if (category == "DAILY_BONUS" && countToday >= 1) {
            return@withContext Result.failure(Exception("You have already claimed today's check-in bonus! Come back tomorrow."))
        }
        if (category == "WATCH_AD" && countToday >= 10) {
            return@withContext Result.failure(Exception("Daily ad limit reached for this task (10/10)."))
        }

        // Record completion
        taskDao.recordCompletion(
            TaskCompletionEntity(
                userId = userId,
                taskId = taskId,
                earnedAmount = reward,
                notesOrProof = proofNotes
            )
        )

        // Credit user balance
        userDao.addEarnings(userId, reward)

        // Insert transaction record
        val txType = if (category == "DAILY_BONUS") "DAILY_BONUS" else "TASK_EARNING"
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                title = "Reward: $taskTitle",
                type = txType,
                amount = reward,
                isCredit = true,
                status = "COMPLETED",
                reference = "TASK-${System.currentTimeMillis().toString().takeLast(6)}"
            )
        )

        // Referral commission for sponsor (15% of task reward)
        val sponsor = userDao.getUserByRefId(user.referredBy)
        if (sponsor != null && sponsor.id != userId) {
            val sponsorCommission = (reward * 0.15 * 100.0).toInt() / 100.0
            if (sponsorCommission > 0.0) {
                userDao.addEarnings(sponsor.id, sponsorCommission)
                transactionDao.insertTransaction(
                    TransactionEntity(
                        userId = sponsor.id,
                        title = "15% Referral Commission from ${user.fullName}",
                        type = "REFERRAL_COMMISSION",
                        amount = sponsorCommission,
                        isCredit = true,
                        status = "COMPLETED",
                        reference = "COMM-${System.currentTimeMillis().toString().takeLast(6)}"
                    )
                )
            }
        }

        Result.success(reward)
    }

    suspend fun requestWithdrawal(
        userId: Long,
        amount: Double,
        method: String,
        accountTitle: String,
        accountNumber: String,
        pin: String
    ): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))

        if (amount < 5.0) {
            return@withContext Result.failure(Exception("Minimum withdrawal amount is $5.00 USD (or Rs. 1,400 PKR)."))
        }

        if (user.balance < amount) {
            return@withContext Result.failure(Exception("Insufficient balance. Available: $${String.format("%.2f", user.balance)}"))
        }

        if (user.securityPin.isNotBlank() && user.securityPin != pin) {
            return@withContext Result.failure(Exception("Invalid Security PIN entered."))
        }

        val fee = 0.0 // 0% fee on Denvork
        val netAmount = amount - fee
        val trxId = "DW" + (100000 + (Math.random() * 900000).toInt()).toString()

        val withdrawal = WithdrawalEntity(
            userId = userId,
            amount = amount,
            fee = fee,
            netAmount = netAmount,
            method = method,
            accountTitle = accountTitle.trim(),
            accountNumber = accountNumber.trim(),
            status = "PENDING",
            trxId = trxId
        )

        // Deduct from user balance
        userDao.deductWithdrawal(userId, amount)

        val withdrawalId = withdrawalDao.insertWithdrawal(withdrawal)

        // Insert transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                userId = userId,
                title = "Withdrawal to $method ($accountNumber)",
                type = "WITHDRAWAL",
                amount = amount,
                isCredit = false,
                status = "PROCESSING",
                reference = trxId
            )
        )

        Result.success(withdrawal.copy(id = withdrawalId))
    }

    suspend fun updatePaymentMethods(
        userId: Long,
        jazzCash: String,
        easypaisa: String,
        bank: String,
        crypto: String
    ) = withContext(Dispatchers.IO) {
        userDao.updatePaymentMethods(userId, jazzCash.trim(), easypaisa.trim(), bank.trim(), crypto.trim())
    }

    suspend fun updateSecurityPin(userId: Long, pin: String) = withContext(Dispatchers.IO) {
        userDao.updateSecurityPin(userId, pin.trim())
    }

    suspend fun verifyKyc(userId: Long) = withContext(Dispatchers.IO) {
        userDao.verifyKyc(userId)
    }

    suspend fun changePassword(userId: Long, oldPass: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByIdSync(userId) ?: return@withContext Result.failure(Exception("User not found"))
        if (user.passwordHash != hashPassword(oldPass)) {
            return@withContext Result.failure(Exception("Current password does not match."))
        }
        userDao.updatePassword(userId, hashPassword(newPass))
        Result.success(Unit)
    }

    suspend fun getTodayEarnings(userId: Long): Double = withContext(Dispatchers.IO) {
        transactionDao.getTodayEarnings(userId, getStartOfTodayMillis())
    }

    suspend fun getTodayTasksDone(userId: Long): Int = withContext(Dispatchers.IO) {
        taskDao.getTotalCompletedToday(userId, getStartOfTodayMillis())
    }

    private fun getStartOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
