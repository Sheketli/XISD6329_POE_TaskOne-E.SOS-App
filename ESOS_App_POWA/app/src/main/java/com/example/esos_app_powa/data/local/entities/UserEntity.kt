package com.example.esos_app_powa.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "users"
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String, // Encrypted
    
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String, // Encrypted, Unique (handled in DAO or manually)
    
    @ColumnInfo(name = "email")
    val email: String, // Encrypted, Unique
    
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    
    @ColumnInfo(name = "medical_info")
    val medicalInfo: String?, // Encrypted
    
    @ColumnInfo(name = "is_active", defaultValue = "1")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "preferred_language", defaultValue = "en")
    val preferredLanguage: String = "en",
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)
