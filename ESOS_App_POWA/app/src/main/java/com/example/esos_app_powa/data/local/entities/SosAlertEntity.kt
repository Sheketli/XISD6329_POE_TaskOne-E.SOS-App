package com.example.esos_app_powa.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

enum class AlertType {
    SOS, SILENT
}

enum class AlertStatus {
    ACTIVE, RESOLVED, CANCELLED
}

@Entity(
    tableName = "sos_alerts",
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
data class SosAlertEntity(
    @PrimaryKey
    @ColumnInfo(name = "alert_id")
    val alertId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "alert_type")
    val alertType: AlertType,
    
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    
    @ColumnInfo(name = "location_approximate", defaultValue = "0")
    val locationApproximate: Boolean = false,
    
    @ColumnInfo(name = "status")
    val status: AlertStatus,
    
    @ColumnInfo(name = "notifications_sent", defaultValue = "0")
    val notificationsSent: Boolean = false,
    
    @ColumnInfo(name = "triggered_at")
    val triggeredAt: Date,
    
    @ColumnInfo(name = "resolved_at")
    val resolvedAt: Date?,
    
    @ColumnInfo(name = "assigned_operator_id")
    val assignedOperatorId: String? // FK to operators (not provided)
)
