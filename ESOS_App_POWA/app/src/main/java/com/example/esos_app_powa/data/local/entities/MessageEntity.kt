package com.example.esos_app_powa.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "message_id")
    val messageId: String,
    
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    
    @ColumnInfo(name = "recipient_id")
    val recipientId: String,
    
    @ColumnInfo(name = "content_encrypted")
    val contentEncrypted: String,
    
    @ColumnInfo(name = "sent_at")
    val sentAt: Date,
    
    @ColumnInfo(name = "is_read", defaultValue = "0")
    val isRead: Boolean = false
)
