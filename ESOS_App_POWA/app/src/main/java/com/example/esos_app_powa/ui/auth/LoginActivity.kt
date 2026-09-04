package com.example.esos_app_powa.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esos_app_powa.R
import com.example.esos_app_powa.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    
    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var progressBar: ProgressBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Check if user is already logged in
        if (auth.currentUser != null) {
            navigateToHome()
            return
        }
        
        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener { performLogin() }
        registerLink.setOnClickListener { navigateToRegister() }
        forgotPasswordLink.setOnClickListener { handleForgotPassword() }
    }
    
    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        // Validation
        if (!validateInputs(email, password)) {
            return
        }
        
        progressBar.visibility = android.view.View.VISIBLE
        loginButton.isEnabled = false
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = android.view.View.GONE
                loginButton.isEnabled = true
                val message = when {
                    exception.message?.contains("user not found") == true -> "Email not registered"
                    exception.message?.contains("wrong password") == true -> "Incorrect password"
                    else -> exception.message ?: "Login failed"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Timber.e(exception, "Login failed")
            }
    }
    
    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                emailEditText.error = getString(R.string.error_field_required)
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailEditText.error = getString(R.string.error_email_invalid)
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
            else -> true
        }
    }
    
    private fun handleForgotPassword() {
        val email = emailEditText.text.toString().trim()
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
