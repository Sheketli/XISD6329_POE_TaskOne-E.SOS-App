package com.example.esos_app_powa.data.local.dao

import androidx.room.*
import com.example.esos_app_powa.data.local.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE (sender_id = :userId AND recipient_id = :otherId) OR (sender_id = :otherId AND recipient_id = :userId) ORDER BY sent_at ASC")
    fun observeChatHistory(userId: String, otherId: String): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET is_read = 1 WHERE recipient_id = :userId AND sender_id = :senderId")
    suspend fun markAsRead(userId: String, senderId: String)
}
