package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
  @PrimaryKey val email: String,
  val name: String,
  val role: String, // "CLIENT", "PROPRIETAIRE", "DEMARCHEUR", "NOTAIRE", "BANQUE", "AGENCE" (Super-Admin)
  val phone: String,
  val codeOtp: String = "",
  val isApproved: Boolean = false, // Has passed Privacy OTP sas entrance
  val subscriptionPaid: Boolean = true, // To verify subscription fee validation
  val walletBalance: Double = 0.0,
  val isSuspended: Boolean = false
)
