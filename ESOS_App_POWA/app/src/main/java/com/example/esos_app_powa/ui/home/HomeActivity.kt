package com.example.esos_app_powa.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esos_app_powa.R
import com.example.esos_app_powa.data.model.SOSAlert
import com.example.esos_app_powa.data.model.EmergencyContact
import com.example.esos_app_powa.data.repository.SosRepository
import com.example.esos_app_powa.ui.auth.LoginActivity
import com.example.esos_app_powa.ui.emergency.EmergencyDirectoryActivity
import com.example.esos_app_powa.ui.incident.IncidentReportActivity
import com.example.esos_app_powa.ui.location.LocationSharingActivity
import com.example.esos_app_powa.ui.messaging.MessagingActivity
import com.example.esos_app_powa.ui.profile.ProfileActivity
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var sosRepository: SosRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private lateinit var sosButton: FrameLayout
    private lateinit var sosButtonText: TextView
    private lateinit var findShelterButton: Button
    private lateinit var reportIncidentButton: Button
    private lateinit var emergencyContactsButton: Button
    private lateinit var secureChatButton: Button
    private lateinit var quickExitButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var gpsStatusText: TextView
    private lateinit var networkStatusText: TextView
    
    private var userLocation: Location? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Check authentication
        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }
        
        initializeViews()
        setupClickListeners()
        requestLocationPermission()
        updateLocationStatus()
    }
    
    private fun initializeViews() {
        sosButton = findViewById(R.id.sosButton)
        sosButtonText = findViewById(R.id.sosButtonText)
        findShelterButton = findViewById(R.id.findShelterButton)
        reportIncidentButton = findViewById(R.id.reportIncidentButton)
        emergencyContactsButton = findViewById(R.id.emergencyContactsButton)
        secureChatButton = findViewById(R.id.secureChatButton)
        quickExitButton = findViewById(R.id.quickExitButton)
        menuButton = findViewById(R.id.menuButton)
        progressBar = findViewById(R.id.progressBar)
        gpsStatusText = findViewById(R.id.gpsStatusText)
        networkStatusText = findViewById(R.id.networkStatusText)
    }
    
    private fun setupClickListeners() {
        sosButton.setOnClickListener { triggerSOSAlert() }
        findShelterButton.setOnClickListener { navigateToDirectory() }
        reportIncidentButton.setOnClickListener { navigateToIncidentReport() }
        emergencyContactsButton.setOnClickListener { navigateToLocation() }
        secureChatButton.setOnClickListener { navigateToMessaging() }
        quickExitButton.setOnClickListener { handleQuickExit() }
        menuButton.setOnClickListener { navigateToProfile() }
    }
    
    private fun triggerSOSAlert() {
        progressBar.visibility = android.view.View.VISIBLE
        sosButton.isEnabled = false
        
        // Get current location
        getCurrentLocation { location ->
            if (location != null) {
                userLocation = location
                createSOSAlert(location.latitude, location.longitude, false)
            } else {
                // Use last known location
                getLastKnownLocation { lastLocation ->
                    if (lastLocation != null) {
                        createSOSAlert(lastLocation.latitude, lastLocation.longitude, true)
                    } else {
                        progressBar.visibility = android.view.View.GONE
                        sosButton.isEnabled = true
                        Toast.makeText(this, getString(R.string.error_location_unavailable), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    private fun createSOSAlert(latitude: Double, longitude: Double, isApproximate: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        
        val alertId = firestore.collection("sos_alerts").document().id
        val alert = SOSAlert(
            alertId = alertId,
            userId = userId,
            alertType = "SOS",
            latitude = latitude,
            longitude = longitude,
            locationApproximate = isApproximate,
            status = "ACTIVE",
            notificationsSent = false,
            triggeredAt = Timestamp.now()
        )
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Save to Repository (Handles Room + Firestore)
                sosRepository.triggerSOS(alert)
                
                // Get emergency contacts and send notifications
                getEmergencyContactsAndNotify(userId)
                
                progressBar.visibility = android.view.View.GONE
                sosButton.isEnabled = true
                
                Toast.makeText(this@HomeActivity, getString(R.string.alert_sent), Toast.LENGTH_SHORT).show()
                
                // Show alert confirmation
                navigateToSOSConfirmation(alertId, latitude, longitude)
            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                sosButton.isEnabled = true
                Toast.makeText(this@HomeActivity, "Failed to save alert locally: ${e.message}", Toast.LENGTH_SHORT).show()
                Timber.e(e, "Failed to create SOS alert")
            }
        }
    }
    
    private fun getEmergencyContactsAndNotify(userId: String) {
        firestore.collection("users").document(userId).collection("emergency_contacts")
            .get()
            .addOnSuccessListener { snapshot ->
                val contacts = snapshot.toObjects(EmergencyContact::class.java)
                if (contacts.isNotEmpty()) {
                    // In a real app, you would send SMS/Email/Push notifications here
                    Timber.d("Sending notifications to ${contacts.size} contacts")
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get emergency contacts")
            }
    }
    
    private fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null)
            return
        }
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get real-time location")
                callback(null)
            }
    }
    
    private fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null)
            return
        }
        
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                callback(location)
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get last known location")
                callback(null)
            }
    }
    
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            updateLocationStatus()
        }
    }
    
    private fun updateLocationStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gpsStatusText.text = getString(R.string.gps_active)
            gpsStatusText.setTextColor(ContextCompat.getColor(this, R.color.success_green))
        } else {
            gpsStatusText.text = "GPS Disabled"
            gpsStatusText.setTextColor(ContextCompat.getColor(this, R.color.warning_amber))
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLocationStatus()
            }
        }
    }
    
    private fun handleQuickExit() {
        // Quickly navigate away from the app
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
    
    private fun navigateToDirectory() {
        val intent = Intent(this, EmergencyDirectoryActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToIncidentReport() {
        val intent = Intent(this, IncidentReportActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToLocation() {
        val intent = Intent(this, LocationSharingActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToMessaging() {
        val intent = Intent(this, MessagingActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToSOSConfirmation(alertId: String, latitude: Double, longitude: Double) {
        val intent = Intent(this, SOSAlertActivity::class.java)
        intent.putExtra("alertId", alertId)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
