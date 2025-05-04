package com.example.smdproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupPage: AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Try to enable Firebase persistence - note this should be in Application class
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.e("Firebase", "Error setting persistence", e)
        }

        // Initialize UI elements
        initializeViews()

        // Set onClickListeners
        setupClickListeners()

        // Initialize Firebase Database
        initializeFirebase()
    }

    private fun initializeViews() {
        nameEditText = findViewById(R.id.editTextText4)
        emailEditText = findViewById(R.id.editTextTextEmailAddress3)
        phoneEditText = findViewById(R.id.editTextPhone2)
        passwordEditText = findViewById(R.id.editTextTextPassword4)
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword5)
        createAccountButton = findViewById(R.id.button2)

        // Find login text view (if it exists in your layout)
        loginTextView = findViewById(R.id.textViewLogin)
    }

    private fun setupClickListeners() {
        // Back button click listener
        val arrowBack = findViewById<ImageView>(R.id.imageView14)
        arrowBack.setOnClickListener {
            // Properly finish this activity rather than starting a new one
            finish()
        }

        // Create account button click listener
        createAccountButton.setOnClickListener {
            if (validateInputs()) {
                if (isNetworkAvailable()) {
                    createAccountWithFirebase()
                } else {
                    createAccountLocally()
                }
            }
        }

        // Login text view click listener (if available)
        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun initializeFirebase() {
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("message")
    }

    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Check if fields are empty
        if (name.isEmpty()) {
            showToast("Please enter your name")
            return false
        }

        if (email.isEmpty()) {
            showToast("Please enter your email")
            return false
        }

        if (!isValidEmail(email)) {
            showToast("Please enter a valid email address")
            return false
        }

        if (phone.isEmpty()) {
            showToast("Please enter your phone number")
            return false
        }

        if (password.isEmpty()) {
            showToast("Please enter a password")
            return false
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters long")
            return false
        }

        if (confirmPassword.isEmpty()) {
            showToast("Please confirm your password")
            return false
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun createAccountWithFirebase() {
        // Show loading state
        createAccountButton.isEnabled = false
        createAccountButton.text = "Creating Account..."

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                addUserToFirebase()
                showToast("Account created successfully!")
                navigateToLogin()
            }
            .addOnFailureListener { e ->
                createAccountButton.isEnabled = true
                createAccountButton.text = "CREATE ACCOUNT"
                showToast("Failed to create account: ${e.message}")
            }
    }

    private fun createAccountLocally() {
        val dbHelper = DatabaseHelper(this)

        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        val id = dbHelper.addUser(name, phone, email, password)
        if (id > 0) {
            showToast("Account created successfully! (Offline mode)")
            navigateToLogin()
        } else {
            showToast("Failed to create account locally.")
        }
    }

    private fun addUserToFirebase() {
        val userId = mAuth.currentUser?.uid

        if (userId != null) {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            val user = hashMapOf(
                "name" to name,
                "phone" to phone,
                "email" to email
            )

            GlobalScope.launch(Dispatchers.IO) {
                val userRef = database.getReference("Users").child(userId)
                userRef.setValue(user)
                    .addOnSuccessListener {
                        // Success - no need to show another toast as we already showed one
                    }
                    .addOnFailureListener {
                        runOnUiThread {
                            showToast("Account created but failed to save user details")
                        }
                    }
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginPage::class.java))
        finish() // Close this activity
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}