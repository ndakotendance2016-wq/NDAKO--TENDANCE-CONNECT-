package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_transactions")
data class BusinessTransaction(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val type: String, // "COMMISSION", "ABONNEMENT", "DOSSIER_BANCAIRE" (bank application), "ACTE_NOTARIE" (notary validation)
  val requesterEmail: String,
  val targetEmail: String, // Bank, Notary, Admin or Owner
  val propertyId: Int,
  val propertyTitle: String,
  val amount: Double,
  val status: String, // "EN_ATTENTE" (pending), "VALIDE" (approved), "REJETE" (rejected)
  val comments: String = "",
  val paymentMethod: String = "", // "MTN MoMo", "Airtel Money", "Virement Bancaire", "Visa/Mastercard"
  val date: Long = System.currentTimeMillis()
)
