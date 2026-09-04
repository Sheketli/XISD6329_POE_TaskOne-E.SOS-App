package com.example.esos_app_powa.di

import android.content.Context
import com.example.esos_app_powa.data.local.AppDatabase
import com.example.esos_app_powa.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        // In a production app, the passphrase should be stored securely (e.g., Keystore)
        // For this implementation, we'll use a placeholder passphrase
        val passphrase = "powa_secure_passphrase".toByteArray()
        return AppDatabase.getDatabase(context, passphrase)
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideSosAlertDao(database: AppDatabase): SosAlertDao = database.sosAlertDao()

    @Provides
    fun provideIncidentReportDao(database: AppDatabase): IncidentReportDao = database.incidentReportDao()

    @Provides
    fun provideEmergencyContactDao(database: AppDatabase): EmergencyContactDao = database.emergencyContactDao()

    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()
}
