package com.example.esos_app_powa.ui.emergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.esos_app_powa.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmergencyDirectoryActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_directory)
        
        setupBackButton()
        setupEmergencyContacts()
    }
    
    private fun setupBackButton() {
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }
    }
    
    private fun setupEmergencyContacts() {
        val emergencyNumbers = listOf(
            Triple("SAPS Emergency", "10111", "South African Police Service"),
            Triple("GBV Command Centre (24/7)", "0800 428 428", "Gender-Based Violence Support"),
            Triple("Childline SA", "0800 055 555", "Child Support"),
            Triple("Legal Aid SA", "0800 110 110", "Legal Support"),
            Triple("Ambulance", "10177", "Medical Emergency"),
            Triple("Emergency SMS Line", "31276", "For people with disabilities")
        )
        
        val contactContainer = findViewById<LinearLayout>(R.id.contactContainer)
        
        for ((name, number, description) in emergencyNumbers) {
            addEmergencyContactCard(contactContainer, name, number, description)
        }
    }
    
    private fun addEmergencyContactCard(container: LinearLayout, name: String, number: String, description: String) {
        val card = layoutInflater.inflate(R.layout.item_emergency_contact, container, false)
        
        val contactNameView = card.findViewById<TextView>(R.id.contactName)
        val contactNumberView = card.findViewById<TextView>(R.id.contactNumber)
        val contactDescriptionView = card.findViewById<TextView>(R.id.contactDescription)
        val callButton = card.findViewById<TextView>(R.id.callButton)
        
        contactNameView.text = name
        contactNumberView.text = number
        contactDescriptionView.text = description
        
        callButton.setOnClickListener {
            makeCall(number)
        }
        
        card.setOnClickListener {
            makeCall(number)
        }
        
        container.addView(card)
    }
    
    private fun makeCall(phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
        }
        
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open dialer", Toast.LENGTH_SHORT).show()
        }
    }
}
