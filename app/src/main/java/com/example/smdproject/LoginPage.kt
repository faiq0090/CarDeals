package com.example.smdproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginPage : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()
        dbHelper = DatabaseHelper(this)

        // Check if user is already logged in
        if (mAuth.currentUser != null) {
            // User is already logged in, redirect to HomeScreenActivity
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
            return
        }

        // User is not logged in, show login screen
        setContentView(R.layout.login_page)

        try {
            // Initialize Firebase persistence if not already enabled
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.e("Firebase", "Firebase persistence already enabled")
        }

        // Get connectivity status
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected

        // Find views from the updated layout
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val guestLoginButton = findViewById<Button>(R.id.guestLoginButton)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)

        // Set up login button click listener
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            // Validation
            if (email.isEmpty()) {
                emailField.error = "Email is required"
                emailField.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordField.error = "Password is required"
                passwordField.requestFocus()
                return@setOnClickListener
            }

            // Login logic based on connectivity
            if (isConnected) {
                // Online login using Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        // Navigate to home screen
                        startActivity(Intent(this, HomeScreenActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Signin_Error", exception.message.toString())
                        Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                // Offline login using local database
                val isAuthenticated = dbHelper.checkUser(email, password)

                if (isAuthenticated) {
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login credentials do not match or no offline data available", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Set up guest login button click listener
        guestLoginButton.setOnClickListener {
            // Sign out any previously logged-in user
            mAuth.signOut()

            // Navigate to home screen as guest
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        // Set up sign up button click listener
        signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignupPage::class.java))
        }

        // Set up forgot password click listener
        forgotPasswordTextView.setOnClickListener {
            val email = emailField.text.toString().trim()

            if (email.isEmpty()) {
                emailField.error = "Enter your email to reset password"
                emailField.requestFocus()
                return@setOnClickListener
            }

            if (isConnected) {
                mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to send reset email: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Internet connection required to reset password", Toast.LENGTH_LONG).show()
            }
        }
    }
}