package com.example.esos_app_powa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.esos_app_powa.data.local.converters.AppTypeConverters
import com.example.esos_app_powa.data.local.dao.*
import com.example.esos_app_powa.data.local.entities.*
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        UserEntity::class,
        SosAlertEntity::class,
        IncidentReportEntity::class,
        EmergencyContactEntity::class,
        MessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sosAlertDao(): SosAlertDao
    abstract fun incidentReportDao(): IncidentReportDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, passphrase: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "esos_database"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
