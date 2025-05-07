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
import com.android.volley.toolbox.StringRequest
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
        val url = ApiConf.BASEURL + "Auth/login.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    if (status == "success") {
                        val userId = jsonResponse.getString("user_id")
                        saveUserSession(userId)
                        startActivity(Intent(this, HomeScreenActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show()
                    Log.e("VolleyLogin", "Parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("VolleyLogin", "Error: ${error.message}")
                Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email
                params["password"] = password
                return params
            }
        }

        queue.add(request)
    }


    private fun saveUserSession(id: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_id", id)
            .apply()



    }

}
