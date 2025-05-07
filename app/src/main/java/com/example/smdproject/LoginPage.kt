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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smdproject.com.example.smdproject.apiconfig.ApiConf
import org.json.JSONObject

class LoginPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val guestLoginButton = findViewById<Button>(R.id.guestLoginButton)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            if (email.isEmpty()) {
                emailField.error = "Email is required"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordField.error = "Password is required"
                return@setOnClickListener
            }

            loginWithVolley(email, password)
        }

        guestLoginButton.setOnClickListener {
            // Guest login: no user ID needed
            startActivity(Intent(this, HomeScreenActivity::class.java))
            finish()
        }

        signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignupPage::class.java))
        }

        forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Forgot password functionality is not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginWithVolley(email: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        val url = ApiConf.BASEURL+"Auth/login.php" // replace with your actual endpoint and port

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val userId = response.getString("user_id")
                    saveUserId(userId)
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show()
                    Log.e("inv",e.message.toString())
                }
            },
            { error ->
                Log.e("VolleyLogin", "Error: ${error.message}")
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        queue.add(request)
    }

    private fun saveUserId(id: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit().putString("user_id", id).apply()
    }
}
