package com.example.esos_app_powa.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class User(
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val medicalInfo: String = "",
    val isActive: Boolean = true,
    val preferredLanguage: String = "en",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
) : Serializable

data class EmergencyContact(
    val contactId: String = "",
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val relationship: String = "",
    val notifySms: Boolean = true,
    val notifyEmail: Boolean = true
) : Serializable

data class SOSAlert(
    val alertId: String = "",
    val userId: String = "",
    val alertType: String = "SOS", // SOS or SILENT
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationApproximate: Boolean = false,
    val status: String = "ACTIVE", // ACTIVE, RESOLVED, CANCELLED
    val notificationsSent: Boolean = false,
    val triggeredAt: Timestamp = Timestamp.now(),
    val resolvedAt: Timestamp? = null,
    val assignedOperatorId: String? = null
) : Serializable

data class IncidentReport(
    val reportId: String = "",
    val userId: String = "",
    val incidentType: String = "", // Physical, Emotional, Sexual, Harassment, Other
    val description: String = "",
    val incidentLocation: String = "",
    val dateOccurred: Timestamp = Timestamp.now(),
    val mediaFileUrls: List<String> = emptyList(),
    val status: String = "SUBMITTED",
    val submittedAt: Timestamp = Timestamp.now(),
    val referenceNumber: String = ""
) : Serializable

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val contentEncrypted: String = "",
    val sentAt: Timestamp = Timestamp.now(),
    val isRead: Boolean = false
) : Serializable

data class AlertHistory(
    val alertId: String = "",
    val userId: String = "",
    val alertType: String = "",
    val status: String = "",
    val location: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val numberOfContacts: Int = 0
) : Serializable
