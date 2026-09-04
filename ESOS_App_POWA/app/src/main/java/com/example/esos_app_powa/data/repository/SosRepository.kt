package com.example.esos_app_powa.data.repository

import com.example.esos_app_powa.data.local.dao.SosAlertDao
import com.example.esos_app_powa.data.local.entities.AlertStatus
import com.example.esos_app_powa.data.local.entities.AlertType
import com.example.esos_app_powa.data.local.entities.SosAlertEntity
import com.example.esos_app_powa.data.model.SOSAlert
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SosRepository @Inject constructor(
    private val sosAlertDao: SosAlertDao,
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {
    suspend fun triggerSOS(alert: SOSAlert) = withContext(Dispatchers.IO) {
        try {
            // 1. Store in local Room (Offline first)
            val entity = SosAlertEntity(
                alertId = alert.alertId,
                userId = alert.userId,
                alertType = AlertType.valueOf(alert.alertType),
                latitude = alert.latitude,
                longitude = alert.longitude,
                locationApproximate = alert.locationApproximate,
                status = AlertStatus.valueOf(alert.status),
                notificationsSent = alert.notificationsSent,
                triggeredAt = alert.triggeredAt.toDate(),
                resolvedAt = alert.resolvedAt?.toDate(),
                assignedOperatorId = alert.assignedOperatorId
            )
            sosAlertDao.insertAlert(entity)
            Timber.d("SOS Alert saved to Room: ${alert.alertId}")

            // 2. Sync to Firestore (Background)
            firestore.collection("sos_alerts").document(alert.alertId).set(alert)
                .addOnSuccessListener { Timber.d("SOS Alert synced to Firestore: ${alert.alertId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync SOS Alert to Firestore") }

            // 3. Sync to Realtime Database (Critical for real-time tracking)
            database.getReference("sos_alerts").child(alert.alertId).setValue(alert)
                .addOnSuccessListener { Timber.d("SOS Alert synced to RTDB: ${alert.alertId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync SOS Alert to RTDB") }

        } catch (e: Exception) {
            Timber.e(e, "Error triggering SOS: ${alert.alertId}")
            throw e
        }
    }
}
