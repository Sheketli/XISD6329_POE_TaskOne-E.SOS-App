package com.example.esos_app_powa.data.local.converters

import androidx.room.TypeConverter
import com.example.esos_app_powa.data.local.entities.AlertStatus
import com.example.esos_app_powa.data.local.entities.AlertType
import java.util.Date

class AppTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromAlertType(value: String): AlertType {
        return AlertType.valueOf(value)
    }

    @TypeConverter
    fun alertTypeToString(type: AlertType): String {
        return type.name
    }

    @TypeConverter
    fun fromAlertStatus(value: String): AlertStatus {
        return AlertStatus.valueOf(value)
    }

    @TypeConverter
    fun alertStatusToString(status: AlertStatus): String {
        return status.name
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
