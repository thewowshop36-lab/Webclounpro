package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DenvorkDatabase
import com.example.data.local.entity.ReferralEntity
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.data.repository.DenvorkRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DenvorkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DenvorkRepository(DenvorkDatabase.getDatabase(application))

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getUser(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getTransactions(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referrals: StateFlow<List<ReferralEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getReferrals(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<WithdrawalEntity>> = _currentUserId.flatMapLatest { id ->
        if (id != null) repository.getWithdrawals(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todayEarnings = MutableStateFlow(0.0)
    val todayEarnings: StateFlow<Double> = _todayEarnings.asStateFlow()

    private val _todayTasksDone = MutableStateFlow(0)
    val todayTasksDone: StateFlow<Int> = _todayTasksDone.asStateFlow()

    // Currency toggle: USD ($) or PKR (Rs.)
    private val _isPkr = MutableStateFlow(false)
    val isPkr: StateFlow<Boolean> = _isPkr.asStateFlow()
    val usdToPkrRate = 280.0

    // Active task being watched/completed in interactive dialog
    private val _activeTask = MutableStateFlow<TaskEntity?>(null)
    val activeTask: StateFlow<TaskEntity?> = _activeTask.asStateFlow()

    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initSeedDataIfEmpty()
            // Auto login to default sponsor account initially or allow new sign in
            val db = DenvorkDatabase.getDatabase(application)
            val sponsor = db.userDao().getUserByRefId("Njk4MDc4")
            if (sponsor != null) {
                _currentUserId.value = sponsor.id
                refreshTodayStats(sponsor.id)
            }
        }
    }

    fun toggleCurrency() {
        _isPkr.value = !_isPkr.value
    }

    fun formatMoney(amount: Double): String {
        return if (_isPkr.value) {
            "Rs. " + String.format("%,.0f", amount * usdToPkrRate)
        } else {
            "$" + String.format("%.2f", amount)
        }
    }

    fun refreshTodayStats(userId: Long) {
        viewModelScope.launch {
            _todayEarnings.value = repository.getTodayEarnings(userId)
            _todayTasksDone.value = repository.getTodayTasksDone(userId)
        }
    }

    fun openTask(task: TaskEntity) {
        _activeTask.value = task
    }

    fun closeActiveTask() {
        _activeTask.value = null
    }

    fun register(
        fullName: String,
        email: String,
        phone: String,
        pass: String,
        referredBy: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || pass.length < 6) {
            sendMessage("Please provide valid details (Password min 6 characters).")
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.registerUser(fullName, email, phone, pass, referredBy)
            _isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUserId.value = user.id
                refreshTodayStats(user.id)
                sendMessage("Welcome to Denvork! $2.00 signup bonus credited.")
                onSuccess()
            }.onFailure { err ->
                sendMessage(err.message ?: "Registration failed.")
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            sendMessage("Please enter your email and password.")
            return
        }

        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.loginUser(email, pass)
            _isAuthLoading.value = false
            result.onSuccess { user ->
                _currentUserId.value = user.id
                refreshTodayStats(user.id)
                sendMessage("Logged in successfully as ${user.fullName}!")
                onSuccess()
            }.onFailure { err ->
                sendMessage(err.message ?: "Login failed.")
            }
        }
    }

    fun switchToUser(user: UserEntity) {
        _currentUserId.value = user.id
        refreshTodayStats(user.id)
        sendMessage("Switched active profile to ${user.fullName}")
    }

    fun logout() {
        _currentUserId.value = null
        sendMessage("Signed out successfully.")
    }

    fun completeActiveTask(task: TaskEntity, proofOrNotes: String = "") {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.completeTask(
                userId = uid,
                taskId = task.id,
                taskTitle = task.title,
                category = task.category,
                reward = task.rewardAmount,
                proofNotes = proofOrNotes
            )
            result.onSuccess { rewardEarned ->
                sendMessage("Task completed! +$${String.format("%.2f", rewardEarned)} added to your balance.")
                _activeTask.value = null
                refreshTodayStats(uid)
            }.onFailure { err ->
                sendMessage(err.message ?: "Task submission failed.")
                _activeTask.value = null
            }
        }
    }

    fun claimDailyBonus(task: TaskEntity) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.completeTask(
                userId = uid,
                taskId = task.id,
                taskTitle = task.title,
                category = "DAILY_BONUS",
                reward = task.rewardAmount
            )
            result.onSuccess {
                sendMessage("Daily Streak Bonus claimed! +$${String.format("%.2f", it)} credited.")
                refreshTodayStats(uid)
            }.onFailure { err ->
                sendMessage(err.message ?: "Already claimed today.")
            }
        }
    }

    fun submitWithdrawal(
        amount: Double,
        method: String,
        accountTitle: String,
        accountNumber: String,
        pin: String,
        onSuccess: () -> Unit
    ) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.requestWithdrawal(
                userId = uid,
                amount = amount,
                method = method,
                accountTitle = accountTitle,
                accountNumber = accountNumber,
                pin = pin
            )
            result.onSuccess { req ->
                sendMessage("Withdrawal request of $${String.format("%.2f", req.amount)} submitted successfully! Trx: ${req.trxId}")
                refreshTodayStats(uid)
                onSuccess()
            }.onFailure { err ->
                sendMessage(err.message ?: "Withdrawal failed.")
            }
        }
    }

    fun savePaymentMethods(jazz: String, easy: String, bank: String, crypto: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updatePaymentMethods(uid, jazz, easy, bank, crypto)
            sendMessage("Payment withdrawal accounts updated.")
        }
    }

    fun updateSecurityPin(pin: String) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.updateSecurityPin(uid, pin)
            sendMessage("Security PIN updated successfully.")
        }
    }

    fun verifyKyc() {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.verifyKyc(uid)
            sendMessage("KYC Identity Verified! VIP status unlocked.")
        }
    }

    fun changePassword(oldPass: String, newPass: String, onSuccess: () -> Unit) {
        val uid = _currentUserId.value ?: return
        viewModelScope.launch {
            val result = repository.changePassword(uid, oldPass, newPass)
            result.onSuccess {
                sendMessage("Password updated successfully.")
                onSuccess()
            }.onFailure { err ->
                sendMessage(err.message ?: "Password update failed.")
            }
        }
    }

    private fun sendMessage(msg: String) {
        viewModelScope.launch {
            _messageFlow.emit(msg)
        }
    }
}
