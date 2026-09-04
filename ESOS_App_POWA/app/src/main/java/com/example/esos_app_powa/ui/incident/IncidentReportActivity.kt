package com.example.esos_app_powa.ui.incident

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.esos_app_powa.R
import com.example.esos_app_powa.data.model.IncidentReport
import com.example.esos_app_powa.data.repository.IncidentRepository
import com.example.esos_app_powa.data.repository.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class IncidentReportActivity : AppCompatActivity() {
    
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var incidentRepository: IncidentRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var firestore: FirebaseFirestore
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private lateinit var incidentTypeSpinner: Spinner
    private lateinit var incidentLocationEditText: EditText
    private lateinit var incidentDescriptionEditText: EditText
    private lateinit var useCurrentLocationBtn: TextView
    private lateinit var submitButton: Button
    private lateinit var backButton: ImageButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_report)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        incidentTypeSpinner = findViewById(R.id.incidentTypeSpinner)
        incidentLocationEditText = findViewById(R.id.incidentLocationEditText)
        incidentDescriptionEditText = findViewById(R.id.incidentDescriptionEditText)
        useCurrentLocationBtn = findViewById(R.id.useCurrentLocationBtn)
        submitButton = findViewById(R.id.submitButton)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {
        submitButton.setOnClickListener { submitIncidentReport() }
        backButton.setOnClickListener { finish() }
        useCurrentLocationBtn.setOnClickListener { fetchCurrentLocation() }
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        Toast.makeText(this, "Fetching high-precision real-time location...", Toast.LENGTH_SHORT).show()
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0].getAddressLine(0)
                            incidentLocationEditText.setText(address)
                        } else {
                            incidentLocationEditText.setText("${location.latitude}, ${location.longitude}")
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Geocoding failed for real-time location")
                        incidentLocationEditText.setText("${location.latitude}, ${location.longitude}")
                    }
                } else {
                    Toast.makeText(this, "Unable to get current location. Ensure GPS is set to High Accuracy.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Failed to fetch real-time location")
                Toast.makeText(this, "Location error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun submitIncidentReport() {
        val incidentType = incidentTypeSpinner.selectedItem.toString()
        val location = incidentLocationEditText.text.toString().trim()
        val description = incidentDescriptionEditText.text.toString().trim()
        
        if (location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val user = auth.currentUser ?: return
        val userId = user.uid
        val reportId = UUID.randomUUID().toString()
        val referenceNumber = "RPT-${System.currentTimeMillis()}"
        
        val report = IncidentReport(
            reportId = reportId,
            userId = userId,
            incidentType = incidentType,
            description = description,
            incidentLocation = location,
            dateOccurred = Timestamp.now(),
            status = "SUBMITTED",
            submittedAt = Timestamp.now(),
            referenceNumber = referenceNumber
        )
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var localUser = userRepository.getUser(userId)
                if (localUser == null) {
                    Toast.makeText(this@IncidentReportActivity, "Linking profile...", Toast.LENGTH_SHORT).show()
                    
                    try {
                        val doc = firestore.collection("users").document(userId).get().await()
                        val firestoreUser = doc.toObject(com.example.esos_app_powa.data.model.User::class.java)
                        if (firestoreUser != null) {
                            userRepository.saveUser(firestoreUser)
                            localUser = firestoreUser
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Firestore fetch failed during incident report sync")
                    }

                    if (localUser == null) {
                        val fallbackUser = com.example.esos_app_powa.data.model.User(
                            userId = userId,
                            fullName = user.displayName ?: "User",
                            email = user.email ?: "",
                            phoneNumber = user.phoneNumber ?: ""
                        )
                        userRepository.saveUser(fallbackUser)
                    }
                }

                incidentRepository.submitReport(report)
                Toast.makeText(this@IncidentReportActivity, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@IncidentReportActivity, "Reference: $referenceNumber", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@IncidentReportActivity, "Critical storage error: ${e.message}", Toast.LENGTH_LONG).show()
                Timber.e(e, "Failed to submit incident report after sync attempt")
            }
        }
    }
}
