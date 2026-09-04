package com.example.esos_app_powa.data.local.dao

import androidx.room.*
import com.example.esos_app_powa.data.local.entities.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)

    @Update
    suspend fun updateContact(contact: EmergencyContactEntity)

    @Query("SELECT * FROM emergency_contacts WHERE user_id = :userId")
    fun observeContactsForUser(userId: String): Flow<List<EmergencyContactEntity>>

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)
}
