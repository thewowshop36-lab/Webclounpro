package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DenvorkBackground
import com.example.ui.theme.DenvorkCard
import com.example.ui.theme.DenvorkCardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldWarning
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DenvorkViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    viewModel: DenvorkViewModel,
    user: UserEntity,
    modifier: Modifier = Modifier
) {
    val withdrawals by viewModel.withdrawals.collectAsState()
    val isPkr by viewModel.isPkr.collectAsState()

    val methods = listOf(
        "JazzCash" to Icons.Default.PhoneAndroid,
        "Easypaisa" to Icons.Default.Payments,
        "Bank Transfer" to Icons.Default.AccountBalance,
        "USDT (TRC20)" to Icons.Default.CurrencyBitcoin
    )

    var selectedMethod by remember { mutableStateOf(methods[0].first) }
    var withdrawAmountText by remember { mutableStateOf("10") }
    var accountTitle by remember { mutableStateOf(user.fullName) }
    var accountNumber by remember {
        mutableStateOf(
            if (user.paymentJazzCash.isNotBlank()) user.paymentJazzCash else user.phone
        )
    }
    var securityPin by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DenvorkBackground)
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Denvork Payout Wallet",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Instant & 24h Payouts to JazzCash, Easypaisa, Bank & USDT",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        // Available Balance Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DenvorkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Available for Withdrawal", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.formatMoney(user.balance),
                            color = EmeraldLight,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldWarning.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Min: $5.00 USD", color = GoldWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fee: 0% (Free)", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        // Request Withdrawal Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DenvorkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DenvorkCardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Request Payout", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Payment Gateway", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Gateway Selector Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        methods.take(2).forEach { (methodName, icon) ->
                            val isSelected = selectedMethod == methodName
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFF0F1829))
                                    .border(
                                        1.dp,
                                        if (isSelected) EmeraldPrimary else DenvorkCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedMethod = methodName
                                        accountNumber = when (methodName) {
                                            "JazzCash" -> user.paymentJazzCash.ifBlank { user.phone }
                                            "Easypaisa" -> user.paymentEasypaisa.ifBlank { user.phone }
                                            "Bank Transfer" -> user.paymentBank
                                            else -> user.paymentCrypto
                                        }
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = if (isSelected) EmeraldPrimary else TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = methodName,
                                        color = if (isSelected) EmeraldLight else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        methods.drop(2).forEach { (methodName, icon) ->
                            val isSelected = selectedMethod == methodName
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFF0F1829))
                                    .border(
                                        1.dp,
                                        if (isSelected) EmeraldPrimary else DenvorkCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedMethod = methodName
                                        accountNumber = when (methodName) {
                                            "Bank Transfer" -> user.paymentBank
                                            else -> user.paymentCrypto
                                        }
                                    }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = if (isSelected) EmeraldPrimary else TextSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = methodName,
                                        color = if (isSelected) EmeraldLight else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amount input
                    Text("Amount ($ USD)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        placeholder = { Text("Min 5.00", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DenvorkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account Title input
                    Text("Account Holder Name", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = accountTitle,
                        onValueChange = { accountTitle = it },
                        placeholder = { Text("Full Legal Name", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DenvorkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account Number / Address input
                    Text(
                        if (selectedMethod == "USDT (TRC20)") "Wallet Address (TRC20)"
                        else if (selectedMethod == "Bank Transfer") "IBAN / Bank Account Number"
                        else "$selectedMethod Mobile Number",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        placeholder = {
                            Text(
                                if (selectedMethod.contains("USDT")) "T..."
                                else if (selectedMethod.contains("Bank")) "PK64..."
                                else "03001234567",
                                color = TextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_acc_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = DenvorkCardBorder
                        )
                    )

                    // Optional PIN if user set one
                    if (user.securityPin.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Security PIN", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = securityPin,
                            onValueChange = { securityPin = it },
                            placeholder = { Text("Enter 4-digit PIN", color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_pin_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = DenvorkCardBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                            viewModel.submitWithdrawal(
                                amount = amt,
                                method = selectedMethod,
                                accountTitle = accountTitle,
                                accountNumber = accountNumber,
                                pin = securityPin
                            ) {
                                withdrawAmountText = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_withdraw_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Payout Request", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Payout History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.History, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Withdrawal History (${withdrawals.size})", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (withdrawals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DenvorkCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No withdrawal requests yet.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(withdrawals) { item ->
                WithdrawalItemRow(withdrawal = item, formatMoney = viewModel::formatMoney)
            }
        }
    }
}

@Composable
fun WithdrawalItemRow(
    withdrawal: WithdrawalEntity,
    formatMoney: (Double) -> String
) {
    val dateStr = remember(withdrawal.requestedAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(withdrawal.requestedAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DenvorkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DenvorkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = withdrawal.method,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (withdrawal.status) {
                                    "COMPLETED" -> EmeraldPrimary.copy(alpha = 0.2f)
                                    "PROCESSING" -> CyanAccent.copy(alpha = 0.2f)
                                    else -> GoldWarning.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = withdrawal.status,
                            color = when (withdrawal.status) {
                                "COMPLETED" -> EmeraldLight
                                "PROCESSING" -> CyanAccent
                                else -> GoldWarning
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${withdrawal.accountTitle} • ${withdrawal.accountNumber}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$dateStr • ID: ${withdrawal.trxId}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(withdrawal.amount),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text("Fee: $0.00", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}
