package com.example.esos_app_powa.data.local.dao

import androidx.room.*
import com.example.esos_app_powa.data.local.entities.IncidentReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: IncidentReportEntity)

    @Query("SELECT * FROM incident_reports WHERE report_id = :reportId")
    suspend fun getReportById(reportId: String): IncidentReportEntity?

    @Query("SELECT * FROM incident_reports WHERE user_id = :userId ORDER BY submitted_at DESC")
    fun observeReportsForUser(userId: String): Flow<List<IncidentReportEntity>>

    @Delete
    suspend fun deleteReport(report: IncidentReportEntity)
}
