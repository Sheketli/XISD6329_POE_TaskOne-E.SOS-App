package com.example.esos_app_powa.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.esos_app_powa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SOSAlertActivity : AppCompatActivity() {
    
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore
    
    private lateinit var confirmationTitle: TextView
    private lateinit var confirmationMessage: TextView
    private lateinit var locationMessage: TextView
    private lateinit var timestampText: TextView
    private lateinit var resolveButton: Button
    private lateinit var keepActiveButton: Button
    
    private var alertId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_alert)
        
        alertId = intent.getStringExtra("alertId")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        
        initializeViews()
        setupClickListeners()
        displayAlertConfirmation(latitude, longitude)
    }
    
    private fun initializeViews() {
        confirmationTitle = findViewById(R.id.confirmationTitle)
        confirmationMessage = findViewById(R.id.confirmationMessage)
        locationMessage = findViewById(R.id.locationMessage)
        timestampText = findViewById(R.id.timestampText)
        resolveButton = findViewById(R.id.resolveButton)
        keepActiveButton = findViewById(R.id.keepActiveButton)
    }
    
    private fun setupClickListeners() {
        resolveButton.setOnClickListener { resolveAlert() }
        keepActiveButton.setOnClickListener { goBackToHome() }
    }
    
    private fun displayAlertConfirmation(latitude: Double, longitude: Double) {
        confirmationTitle.text = getString(R.string.alert_sent)
        confirmationMessage.text = getString(R.string.alert_sent_message)
        
        locationMessage.text = "Location: $latitude, $longitude"
        
        val dateFormat = SimpleDateFormat("HH:mm:ss, dd MMM yyyy", Locale.getDefault())
        timestampText.text = "Sent: ${dateFormat.format(Date())}"
    }
    
    private fun resolveAlert() {
        if (alertId == null) return
        
        firestore.collection("sos_alerts").document(alertId!!)
            .update("status", "RESOLVED")
            .addOnSuccessListener {
                goBackToHome()
            }
            .addOnFailureListener { exception ->
                android.widget.Toast.makeText(
                    this,
                    "Error resolving alert: ${exception.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
    }
    
    private fun goBackToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}
