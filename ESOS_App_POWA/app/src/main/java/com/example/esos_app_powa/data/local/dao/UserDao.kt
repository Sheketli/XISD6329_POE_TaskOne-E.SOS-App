package com.example.esos_app_powa.data.local.dao

import androidx.room.*
import com.example.esos_app_powa.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun observeUserById(userId: String): Flow<UserEntity?>

    @Delete
    suspend fun deleteUser(user: UserEntity)
}
