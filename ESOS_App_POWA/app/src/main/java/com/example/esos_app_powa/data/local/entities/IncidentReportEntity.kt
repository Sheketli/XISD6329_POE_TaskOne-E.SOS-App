package com.example.esos_app_powa.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "incident_reports",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class IncidentReportEntity(
    @PrimaryKey
    @ColumnInfo(name = "report_id")
    val reportId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "incident_type")
    val incidentType: String,
    
    @ColumnInfo(name = "description")
    val description: String, // Encrypted
    
    @ColumnInfo(name = "incident_location")
    val incidentLocation: String?,
    
    @ColumnInfo(name = "date_occurred")
    val dateOccurred: Date,
    
    @ColumnInfo(name = "media_file_urls")
    val mediaFileUrls: List<String>?, // JSONB mapping
    
    @ColumnInfo(name = "status", defaultValue = "SUBMITTED")
    val status: String = "SUBMITTED",
    
    @ColumnInfo(name = "submitted_at")
    val submittedAt: Date,

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String
)
