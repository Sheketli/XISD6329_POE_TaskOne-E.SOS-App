package com.example.esos_app_powa.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.esos_app_powa.R
import com.example.esos_app_powa.data.model.EmergencyContact
import com.example.esos_app_powa.data.model.User
import com.example.esos_app_powa.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var emergencyContactsContainer: LinearLayout
    private lateinit var editButton: Button
    private lateinit var signOutButton: Button
    private lateinit var deleteAccountButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupClickListeners()
        loadUserProfile()
    }

    private fun initializeViews() {
        fullNameText = findViewById(R.id.fullNameText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
        emergencyContactsContainer = findViewById(R.id.emergencyContactsContainer)
        editButton = findViewById(R.id.editButton)
        signOutButton = findViewById(R.id.signOutButton)
        deleteAccountButton = findViewById(R.id.deleteAccountButton)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener { finish() }
        editButton.setOnClickListener {
            Toast.makeText(this, "Edit Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }
        signOutButton.setOnClickListener { showSignOutConfirmation() }
        deleteAccountButton.setOnClickListener { showDeleteAccountConfirmation() }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    displayUserProfile(user)
                    loadEmergencyContacts(userId)
                }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to load user profile")
                Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayUserProfile(user: User) {
        fullNameText.text = user.fullName
        emailText.text = user.email
        phoneText.text = user.phoneNumber
    }

    private fun loadEmergencyContacts(userId: String) {
        firestore.collection("users").document(userId).collection("emergency_contacts")
            .get()
            .addOnSuccessListener { snapshot ->
                val contacts = snapshot.toObjects(EmergencyContact::class.java)
                displayEmergencyContacts(contacts)
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to load emergency contacts")
            }
    }

    private fun displayEmergencyContacts(contacts: List<EmergencyContact>) {
        emergencyContactsContainer.removeAllViews()
        if (contacts.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = getString(R.string.no_emergency_contacts)
                setPadding(0, 16, 0, 16)
            }
            emergencyContactsContainer.addView(emptyView)
            return
        }

        for (contact in contacts) {
            val card = layoutInflater.inflate(R.layout.item_contact_card, emergencyContactsContainer, false)
            val nameView = card.findViewById<TextView>(R.id.contactName)
            val relationView = card.findViewById<TextView>(R.id.contactRelation)
            val phoneView = card.findViewById<TextView>(R.id.contactPhone)

            nameView.text = contact.name
            relationView.text = contact.relationship
            phoneView.text = contact.phoneNumber

            emergencyContactsContainer.addView(card)
        }
    }

    private fun showSignOutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.sign_out)
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign Out") { _, _ -> performSignOut() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSignOut() {
        auth.signOut()
        navigateToLogin()
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_account)
            .setMessage("WARNING: This will permanently delete your account and all data. This action cannot be undone. Are you sure?")
            .setPositiveButton("Delete Permanently") { _, _ -> performDeleteAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDeleteAccount() {
        val user = auth.currentUser
        val userId = user?.uid ?: return

        // 1. Delete from Firestore
        firestore.collection("users").document(userId).delete()
            .addOnSuccessListener {
                // 2. Delete Auth User
                user.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_LONG).show()
                        navigateToLogin()
                    }
                    .addOnFailureListener { exception ->
                        Timber.e(exception, "Failed to delete auth user")
                        Toast.makeText(this, "Failed to delete account. Please re-authenticate and try again.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed to delete firestore data")
                Toast.makeText(this, "Error deleting data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
