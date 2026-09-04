package com.example.esos_app_powa.data.repository

import com.example.esos_app_powa.data.local.dao.UserDao
import com.example.esos_app_powa.data.local.entities.UserEntity
import com.example.esos_app_powa.data.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {
    /**
     * Saves a user to local Room database, remote Firestore, and Realtime Database.
     */
    suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        try {
            // 1. Map Model to Room Entity
            val userEntity = UserEntity(
                userId = user.userId,
                fullName = user.fullName,
                phoneNumber = user.phoneNumber,
                email = user.email,
                passwordHash = "", 
                medicalInfo = user.medicalInfo,
                isActive = user.isActive,
                preferredLanguage = user.preferredLanguage,
                createdAt = user.createdAt.toDate(),
                updatedAt = user.updatedAt.toDate()
            )

            // 2. Store in local Room database
            userDao.insertUser(userEntity)
            Timber.d("User saved to local Room database: ${user.userId}")

            // Also ensure we have a "system" user for reports if needed, 
            // but usually the constraint error 19 (787) means the parent user is missing.
            // Let's check if the userId matches.

            // 3. Store in Firestore
            firestore.collection("users").document(user.userId).set(user)
                .addOnSuccessListener { Timber.d("User synced to Firestore: ${user.userId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync user to Firestore") }

            // 4. Store in Realtime Database (RTDB)
            database.getReference("users").child(user.userId).setValue(user)
                .addOnSuccessListener { Timber.d("User synced to RTDB: ${user.userId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync user to RTDB") }

        } catch (e: Exception) {
            Timber.e(e, "Error saving user: ${user.userId}")
            throw e
        }
    }

    suspend fun getUser(userId: String): User? = withContext(Dispatchers.IO) {
        // Try Room first for performance
        val localUser = userDao.getUserById(userId)
        if (localUser != null) {
            return@withContext User(
                userId = localUser.userId,
                fullName = localUser.fullName,
                phoneNumber = localUser.phoneNumber,
                email = localUser.email,
                medicalInfo = localUser.medicalInfo ?: "",
                isActive = localUser.isActive,
                preferredLanguage = localUser.preferredLanguage,
                createdAt = Timestamp(localUser.createdAt),
                updatedAt = Timestamp(localUser.updatedAt)
            )
        }
        return@withContext null
    }
}
