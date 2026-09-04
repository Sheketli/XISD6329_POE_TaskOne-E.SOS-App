package com.example.esos_app_powa.data.repository

import com.example.esos_app_powa.data.local.dao.IncidentReportDao
import com.example.esos_app_powa.data.local.entities.IncidentReportEntity
import com.example.esos_app_powa.data.model.IncidentReport
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(
    private val incidentReportDao: IncidentReportDao,
    private val firestore: FirebaseFirestore,
    private val database: FirebaseDatabase
) {
    suspend fun submitReport(report: IncidentReport) = withContext(Dispatchers.IO) {
        try {
            // 1. Store in local Room (Offline first)
            val entity = IncidentReportEntity(
                reportId = report.reportId,
                userId = report.userId,
                incidentType = report.incidentType,
                description = report.description,
                incidentLocation = report.incidentLocation,
                dateOccurred = report.dateOccurred.toDate(),
                mediaFileUrls = report.mediaFileUrls,
                status = report.status,
                submittedAt = report.submittedAt.toDate(),
                referenceNumber = report.referenceNumber
            )
            incidentReportDao.insertReport(entity)
            Timber.d("Incident Report saved to Room: ${report.reportId}")

            // 2. Sync to Firestore (Background)
            firestore.collection("incident_reports").document(report.reportId).set(report)
                .addOnSuccessListener { Timber.d("Incident Report synced to Firestore: ${report.reportId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync Incident Report to Firestore") }

            // 3. Sync to Realtime Database
            database.getReference("incident_reports").child(report.reportId).setValue(report)
                .addOnSuccessListener { Timber.d("Incident Report synced to RTDB: ${report.reportId}") }
                .addOnFailureListener { e -> Timber.e(e, "Failed to sync Incident Report to RTDB") }

        } catch (e: Exception) {
            Timber.e(e, "Error submitting incident report: ${report.reportId}")
            throw e
        }
    }
}
