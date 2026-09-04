package com.example.esos_app_powa.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.esos_app_powa.R
import com.example.esos_app_powa.data.model.EmergencyContact
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationSharingActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private lateinit var locationText: TextView
    private lateinit var contactsContainer: LinearLayout
    private lateinit var stopSharingButton: Button
    private lateinit var updateContactsButton: Button
    private lateinit var backButton: ImageButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_sharing)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        initializeViews()
        setupClickListeners()
        requestLocationPermission()
        loadEmergencyContacts()
    }
    
    private fun initializeViews() {
        locationText = findViewById(R.id.locationText)
        contactsContainer = findViewById(R.id.contactsContainer)
        stopSharingButton = findViewById(R.id.stopSharingButton)
        updateContactsButton = findViewById(R.id.updateContactsButton)
        backButton = findViewById(R.id.backButton)
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        stopSharingButton.setOnClickListener { stopLocationSharing() }
        updateContactsButton.setOnClickListener { updateContactsList() }
    }
    
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            updateCurrentLocation()
        }
    }
    
    private fun updateCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    locationText.text = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}"
                } else {
                    locationText.text = "Location unavailable"
                }
            }
            .addOnFailureListener {
                locationText.text = "Error fetching real-time location"
            }
    }
    
    private fun loadEmergencyContacts() {
        val userId = auth.currentUser?.uid ?: return
        
        firestore.collection("users").document(userId).collection("emergency_contacts")
            .get()
            .addOnSuccessListener { snapshot ->
                contactsContainer.removeAllViews()
                for (doc in snapshot.documents) {
                    val contact = doc.toObject(EmergencyContact::class.java)
                    if (contact != null) {
                        addContactCard(contact)
                    }
                }
            }
    }
    
    private fun addContactCard(contact: EmergencyContact) {
        val card = layoutInflater.inflate(R.layout.item_contact_card, contactsContainer, false)
        
        val contactName = card.findViewById<TextView>(R.id.contactName)
        val contactPhone = card.findViewById<TextView>(R.id.contactPhone)
        
        contactName.text = contact.name
        contactPhone.text = contact.phoneNumber
        
        contactsContainer.addView(card)
    }
    
    private fun stopLocationSharing() {
        Toast.makeText(this, "Location sharing stopped", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun updateContactsList() {
        loadEmergencyContacts()
        Toast.makeText(this, "Contacts updated", Toast.LENGTH_SHORT).show()
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateCurrentLocation()
            }
        }
    }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
