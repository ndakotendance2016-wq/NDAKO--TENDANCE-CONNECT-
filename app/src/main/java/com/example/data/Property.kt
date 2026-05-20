package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val description: String,
  val price: Double,
  val category: String, // "Location Standard", "Location Saisonnière", "Location Journalière", "Vente"
  val location: String, // e.g. "Brazzaville - Centre", "Pointe-Noire - Mpaka", etc.
  val status: String, // "DISPONIBLE", "OCCUPÉ", "EN_TRAVAUX"
  val ownerEmail: String, // References UserAccount
  val ownerName: String,
  val ownerPhone: String,
  val imageResName: String, // "house_luxury_1", "house_luxury_2" etc
  val whatsAppClicks: Int = 0,
  val creationDate: Long = System.currentTimeMillis()
)
