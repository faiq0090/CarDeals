package com.example.smdproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smdproject.com.example.smdproject.apiconfig.ApiConf
import org.json.JSONObject

class SignupPage : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var requestQueue: RequestQueue
    private val apiUrl = ApiConf.BASEURL+"Auth/signup.php" // Replace with your real API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        nameEditText = findViewById(R.id.editTextText4)
        emailEditText = findViewById(R.id.editTextTextEmailAddress3)
        phoneEditText = findViewById(R.id.editTextPhone2)
        passwordEditText = findViewById(R.id.editTextTextPassword4)
        confirmPasswordEditText = findViewById(R.id.editTextTextPassword5)
        createAccountButton = findViewById(R.id.button2)
        loginTextView = findViewById(R.id.textViewLogin)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.imageView14).setOnClickListener {
            finish()
        }

        createAccountButton.setOnClickListener {
            if (validateInputs()) {
                if (isNetworkAvailable()) {
                    createAccountWithVolley()
                } else {
                    showToast("No internet connection")
                }
            }
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (name.isEmpty()) {
            showToast("Please enter your name")
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email")
            return false
        }
        if (phone.isEmpty()) {
            showToast("Please enter your phone number")
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            showToast("Password must be at least 6 characters")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }

        return true
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun createAccountWithVolley() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        createAccountButton.isEnabled = false
        createAccountButton.text = "Creating Account..."

        val stringRequest = object : StringRequest(
            Request.Method.POST, apiUrl,
            Response.Listener { response ->
                createAccountButton.isEnabled = true
                createAccountButton.text = "CREATE ACCOUNT"

                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        showToast("Account created successfully!")
                        navigateToLogin()
                    } else {
                        val error = jsonResponse.optString("error", "Failed to create account")
                        showToast("Error: $error")
                    }
                } catch (e: Exception) {
                    showToast("Response error: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                createAccountButton.isEnabled = true
                createAccountButton.text = "CREATE ACCOUNT"
                showToast("Network error: ${error.message}")
                Log.e("net",error.message.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["email"] = email
                params["phoneno"] = phone
                params["password"] = password
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginPage::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
