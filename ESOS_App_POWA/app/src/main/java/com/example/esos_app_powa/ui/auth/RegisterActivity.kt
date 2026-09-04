package com.example.esos_app_powa.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esos_app_powa.R
import com.example.esos_app_powa.data.model.User
import com.example.esos_app_powa.data.repository.UserRepository
import com.example.esos_app_powa.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var userRepository: UserRepository
    
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var medicalInfoEditText: EditText
    private lateinit var popiaCheckBox: CheckBox
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        medicalInfoEditText = findViewById(R.id.medicalInfoEditText)
        popiaCheckBox = findViewById(R.id.popiaCheckBox)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupClickListeners() {
        registerButton.setOnClickListener { performRegistration() }
        loginLink.setOnClickListener { navigateToLogin() }
    }
    
    private fun performRegistration() {
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val medicalInfo = medicalInfoEditText.text.toString().trim()
        
        // Validation
        if (!validateInputs(fullName, email, phone, password, confirmPassword)) {
            return
        }
        
        if (!popiaCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to POPIA terms", Toast.LENGTH_SHORT).show()
            return
        }
        
        progressBar.visibility = android.view.View.VISIBLE
        registerButton.isEnabled = false
        
        // Create Firebase Authentication account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: ""
                
                // Create user document in Firestore
                val user = User(
                    userId = userId,
                    fullName = fullName,
                    phoneNumber = phone,
                    email = email,
                    medicalInfo = medicalInfo,
                    isActive = true,
                    preferredLanguage = "en",
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                
                // 2. Save user to Repository (Handles both Room and Firestore)
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        userRepository.saveUser(user)
                        progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@RegisterActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    } catch (e: Exception) {
                        progressBar.visibility = android.view.View.GONE
                        registerButton.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Failed to save profile locally: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = android.view.View.GONE
                registerButton.isEnabled = true
                val message = when {
                    exception.message?.contains("already in use") == true -> "Email already registered"
                    exception.message?.contains("weak password") == true -> "Password is too weak"
                    else -> exception.message ?: "Registration failed"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Timber.e(exception, "Registration failed")
            }
    }
    
    private fun validateInputs(fullName: String, email: String, phone: String, password: String, confirmPassword: String): Boolean {
        return when {
            fullName.isEmpty() -> {
                fullNameEditText.error = getString(R.string.error_field_required)
                false
            }
            email.isEmpty() -> {
                emailEditText.error = getString(R.string.error_field_required)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailEditText.error = getString(R.string.error_email_invalid)
                false
            }
            phone.isEmpty() -> {
                phoneEditText.error = getString(R.string.error_field_required)
                false
            }
            !isValidPhoneNumber(phone) -> {
                phoneEditText.error = getString(R.string.error_phone_invalid)
                false
            }
            password.isEmpty() -> {
                passwordEditText.error = getString(R.string.error_field_required)
                false
            }
            password.length < 8 -> {
                passwordEditText.error = getString(R.string.error_password_short)
                false
            }
            password != confirmPassword -> {
                confirmPasswordEditText.error = getString(R.string.error_password_mismatch)
                false
            }
            else -> true
        }
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        // Basic validation for South African phone numbers
        return phone.length >= 10 && phone.all { it.isDigit() || it == '+' }
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
