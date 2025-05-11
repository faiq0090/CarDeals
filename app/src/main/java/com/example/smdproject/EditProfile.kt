package com.example.smdproject

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.HashMap

class EditProfile : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: ImageView

    private val UPDATE_PROFILE_URL = ApiConf.BASEURL + "Users/update_profile.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        emailEditText = findViewById(R.id.email)
        phoneEditText = findViewById(R.id.phone)
        nameEditText = findViewById(R.id.name)
        passwordEditText = findViewById(R.id.pass)
        saveButton = findViewById(R.id.button1)
        backButton = findViewById(R.id.arrow_back22)

        loadUserData()

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserData()
    {
        val sharedPref = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)
       // val userId = 2 // 🔴 Hardcoded for testing

        val queue = Volley.newRequestQueue(this)
        val url = ApiConf.BASEURL + "Users/get_profile.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        emailEditText.setText(jsonResponse.getString("email"))
                        phoneEditText.setText(jsonResponse.getString("phoneno"))
                        nameEditText.setText(jsonResponse.getString("name"))

                        emailEditText.isFocusableInTouchMode = true
                        phoneEditText.isFocusableInTouchMode = true
                        nameEditText.isFocusableInTouchMode = true
                    } else {
                        Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun updateProfile() {
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email and name are required", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

       // val userId = 2 // 🔴 Hardcoded for testing

        saveButton.isEnabled = false

        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, UPDATE_PROFILE_URL,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getString("status") == "success") {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                }
                saveButton.isEnabled = true
            },
            Response.ErrorListener {
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
                saveButton.isEnabled = true
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                params["email"] = email
                params["phoneno"] = phone
                params["name"] = name
                if (password.isNotEmpty()) {
                    params["password"] = password
                }
                return params
            }
        }

        queue.add(stringRequest)
    }
}
