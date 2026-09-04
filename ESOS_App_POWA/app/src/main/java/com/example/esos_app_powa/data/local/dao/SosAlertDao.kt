package com.example.esos_app_powa.data.local.dao

import androidx.room.*
import com.example.esos_app_powa.data.local.entities.SosAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SosAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SosAlertEntity)

    @Update
    suspend fun updateAlert(alert: SosAlertEntity)

    @Query("SELECT * FROM sos_alerts WHERE alert_id = :alertId")
    suspend fun getAlertById(alertId: String): SosAlertEntity?

    @Query("SELECT * FROM sos_alerts WHERE user_id = :userId ORDER BY triggered_at DESC")
    fun observeAlertsForUser(userId: String): Flow<List<SosAlertEntity>>

    @Query("SELECT * FROM sos_alerts WHERE status = 'ACTIVE'")
    fun observeActiveAlerts(): Flow<List<SosAlertEntity>>
}
