package com.example.smdproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class profile : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var switchDarkMode: Switch

    private val PROFILE_API_URL = ApiConf.BASEURL + "Users/get_profile.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Apply saved theme
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val themeMode = sharedPref.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        applyTheme(themeMode)

        // Initialize views
        emailEditText = view.findViewById(R.id.email)
        nameEditText = view.findViewById(R.id.name)
        phoneEditText = view.findViewById(R.id.phone)
        passwordEditText = view.findViewById(R.id.pass)
        switchDarkMode = view.findViewById(R.id.switch1)

        // Disable editing
        emailEditText.isEnabled = false
        nameEditText.isEnabled = false
        phoneEditText.isEnabled = false
        passwordEditText.isEnabled = false

        // Edit button action
        val editButton = view.findViewById<Button>(R.id.Editbutton1)
        editButton.setOnClickListener {
            val intent = Intent(context, EditProfile::class.java)
            startActivity(intent)
        }

        // Theme switch action
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            applyTheme(newTheme)
            saveThemePreference(newTheme)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_id", null)

        if (!userId.isNullOrEmpty()) {
            val queue = Volley.newRequestQueue(requireContext())

            val request = object : StringRequest(
                Request.Method.POST, PROFILE_API_URL,
                Response.Listener { response ->
                    try {
                        val jsonResponse = JSONObject(response)

                        if (jsonResponse.getString("status") == "success") {
                            emailEditText.setText(jsonResponse.getString("email"))
                            nameEditText.setText(jsonResponse.getString("name"))
                            phoneEditText.setText(jsonResponse.getString("phoneno"))
                            passwordEditText.setText("********") // Placeholder
                        } else {
                            Toast.makeText(requireContext(), "Failed to load profile: ${jsonResponse.optString("message", "Unknown error")}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("ProfileFragment", "Error parsing response: ${e.message}")
                        Toast.makeText(requireContext(), "Error loading profile data", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("ProfileFragment", "Error fetching profile: ${error.message}")
                    Toast.makeText(requireContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["user_id"] = userId
                    return params
                }
            }

            queue.add(request)
        } else {
            Toast.makeText(requireContext(), "Please log in to view your profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyTheme(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    private fun saveThemePreference(themeMode: Int) {
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("themeMode", themeMode).apply()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
