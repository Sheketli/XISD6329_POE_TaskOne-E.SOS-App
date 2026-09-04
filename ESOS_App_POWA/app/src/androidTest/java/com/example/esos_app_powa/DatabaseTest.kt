package com.example.esos_app_powa

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.esos_app_powa.data.local.AppDatabase
import com.example.esos_app_powa.data.local.entities.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Using an in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeUserAndReadInList() = runBlocking {
        val user = UserEntity(
            userId = "1",
            fullName = "John Doe",
            phoneNumber = "1234567890",
            email = "john@example.com",
            passwordHash = "hashed_password",
            medicalInfo = "None",
            createdAt = Date(),
            updatedAt = Date()
        )
        db.userDao().insertUser(user)
        val byId = db.userDao().getUserById("1")
        assertEquals(byId?.fullName, "John Doe")
    }
}
