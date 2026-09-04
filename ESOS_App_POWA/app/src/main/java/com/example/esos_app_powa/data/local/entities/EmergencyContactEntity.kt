package com.example.esos_app_powa.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_contacts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class EmergencyContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "contact_id")
    val contactId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String, // Encrypted
    
    @ColumnInfo(name = "email")
    val email: String?, // Optional encrypted email
    
    @ColumnInfo(name = "relationship")
    val relationship: String,
    
    @ColumnInfo(name = "notify_sms", defaultValue = "1")
    val notifySms: Boolean = true,
    
    @ColumnInfo(name = "notify_email", defaultValue = "0")
    val notifyEmail: Boolean = false
)
