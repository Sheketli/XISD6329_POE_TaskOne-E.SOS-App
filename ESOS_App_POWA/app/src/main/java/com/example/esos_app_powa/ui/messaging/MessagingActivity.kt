package com.example.esos_app_powa.ui.messaging

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esos_app_powa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class MessagingActivity : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private lateinit var messageContainer: LinearLayout
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var scrollView: ScrollView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        initializeViews()
        setupClickListeners()
        loadMessages()
    }
    
    private fun initializeViews() {
        messageContainer = findViewById(R.id.messageContainer)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        backButton = findViewById(R.id.backButton)
        scrollView = findViewById(R.id.scrollView)
    }
    
    private fun setupClickListeners() {
        sendButton.setOnClickListener { sendMessage() }
        backButton.setOnClickListener { finish() }
    }
    
    private fun loadMessages() {
        val userId = auth.currentUser?.uid ?: return
        
        // In a real app, you would load messages from Firestore
        // For now, showing demo message
        addMessageBubble("Welcome to Secure Messaging", "POWA Support Team", true, "09:30 AM")
        addMessageBubble("How can we help you today?", "POWA Support Team", true, "09:31 AM")
    }
    
    private fun sendMessage() {
        val message = messageEditText.text.toString().trim()
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        
        val userId = auth.currentUser?.uid ?: return
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(System.currentTimeMillis())
        
        addMessageBubble(message, "You", false, currentTime)
        messageEditText.text.clear()
        
        // Save to Firestore
        val messageData = mapOf(
            "senderId" to userId,
            "content" to message,
            "sentAt" to Timestamp.now(),
            "isRead" to false
        )
        
        firestore.collection("messages").add(messageData)
            .addOnSuccessListener {
                scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                Timber.e(exception, "Failed to send message")
            }
    }
    
    private fun addMessageBubble(message: String, sender: String, isFromOperator: Boolean, time: String) {
        val messageView = layoutInflater.inflate(R.layout.item_message_bubble, messageContainer, false)
        
        val bubbleContainer = messageView.findViewById<LinearLayout>(R.id.bubbleContainer)
        val messageText = messageView.findViewById<TextView>(R.id.messageText)
        val senderText = messageView.findViewById<TextView>(R.id.senderText)
        val timeText = messageView.findViewById<TextView>(R.id.timeText)
        val bubble = messageView.findViewById<LinearLayout>(R.id.messageBubble)
        
        messageText.text = message
        senderText.text = sender
        timeText.text = time
        
        if (isFromOperator) {
            bubble.setBackgroundResource(R.drawable.message_bubble_operator)
            messageText.setTextColor(getColor(R.color.text_primary))
            bubbleContainer.gravity = Gravity.START
        } else {
            bubble.setBackgroundResource(R.drawable.message_bubble_user)
            messageText.setTextColor(getColor(R.color.text_white))
            bubbleContainer.gravity = Gravity.END
        }
        
        messageContainer.addView(messageView)
    }
}
