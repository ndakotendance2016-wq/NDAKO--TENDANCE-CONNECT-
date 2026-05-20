package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
  // --- USER ACCOUNTS ---
  @Query("SELECT * FROM user_accounts")
  fun getAllUserAccounts(): Flow<List<UserAccount>>

  @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
  suspend fun getUserAccountByEmail(email: String): UserAccount?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUserAccount(user: UserAccount)

  @Update
  suspend fun updateUserAccount(user: UserAccount)

  @Delete
  suspend fun deleteUserAccount(user: UserAccount)

  // --- PROPERTIES ---
  @Query("SELECT * FROM properties ORDER BY creationDate DESC")
  fun getAllProperties(): Flow<List<Property>>

  @Query("SELECT * FROM properties WHERE id = :id LIMIT 1")
  suspend fun getPropertyById(id: Int): Property?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProperty(property: Property)

  @Update
  suspend fun updateProperty(property: Property)

  @Delete
  suspend fun deleteProperty(property: Property)

  @Query("DELETE FROM properties WHERE id = :id")
  suspend fun deletePropertyById(id: Int)

  // --- TRANSACTIONS & TRANSFERS ---
  @Query("SELECT * FROM business_transactions ORDER BY date DESC")
  fun getAllTransactions(): Flow<List<BusinessTransaction>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(tx: BusinessTransaction)

  @Update
  suspend fun updateTransaction(tx: BusinessTransaction)
}
