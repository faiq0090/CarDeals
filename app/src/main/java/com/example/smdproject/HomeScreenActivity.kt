package com.example.smdproject

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smdproject.db.CarDatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentUserId: Int? = null
    private var isLoggedIn: Boolean = false

    override fun onStart() {
        super.onStart()
        loadUserSession()

        val url = ApiConf.BASEURL + "Ads/getad.php" // Adjust path as needed
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        val adsArray = response.getJSONArray("ads")
                        val dbHelper = CarDatabaseHelper(this)
                        dbHelper.clearAllCarAds()
                        for (i in 0 until adsArray.length()) {
                            val adObject = adsArray.getJSONObject(i)
                            val values = ContentValues().apply {
                                put("id", adObject.getInt("id"))
                                put("user_id", adObject.getInt("user_id"))
                                put("car_type", adObject.getString("car_type"))
                                put("car_model", adObject.getString("car_model"))
                                put("city", adObject.getString("city"))
                                put("model", adObject.getString("model"))
                                put("registered", adObject.getString("registered"))
                                put("color", adObject.getString("color"))
                                put("fuel_type", adObject.getString("fuel_type"))
                                put("body_type", adObject.getString("body_type"))
                                put("transmission_type", adObject.getString("transmission_type"))
                                // Change these lines
                                put("engine_capacity", adObject.getString("engine_capacity"))
                                put("km_driven", adObject.getString("km_driven"))
                                put("price", adObject.getString("price"))
                                put("description", adObject.getString("description"))
                                put("address", adObject.getString("address"))
                                put("image_base64", adObject.getString("image"))

                            }

                            dbHelper.insertCarAd(values)


                        }

                        Toast.makeText(this, "Ads synced to local database", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No ads found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                    Log.e("VolleyAds", e.toString())
                }
            },
            { error ->
                Toast.makeText(this, "Network error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("VolleyAds", error.toString())
            }
        )

        queue.add(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        loadUserSession()

        // Search bar logic
        val searchView = findViewById<SearchView>(R.id.searchview1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent(this@HomeScreenActivity, SearchResults::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        // Buttons and layout listeners
        setupQuickSearches()

        findViewById<LinearLayout>(R.id.Insurancebtn).setOnClickListener {
            startActivity(Intent(this, InsuranceHome::class.java))
        }

        findViewById<LinearLayout>(R.id.Dealershipbtn).setOnClickListener {
            startActivity(Intent(this, DealershipHome::class.java))
        }

        // Bottom navigation logic
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_chat -> {
                    if (isLoggedIn) replaceFragment(AllChats())
                    else redirectToLogin("Log in to chat.")
                    true
                }

                R.id.action_upload -> {
                    if (isLoggedIn) replaceFragment(sellcar())
                    else redirectToLogin("Log in to post ads.")
                    true
                }

                R.id.action_myads -> {
                    if (isLoggedIn) replaceFragment(Myads())
                    else redirectToLogin("Log in to view your ads.")
                    true
                }

                R.id.action_profile -> {
                    replaceFragment(profile())
                    true
                }

                R.id.action_home -> {
                    finish()
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }

                else -> false
            }
        }
    }

    private fun loadUserSession() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
        val userIdStr = sharedPref.getString("user_id", null)
        currentUserId = if (isLoggedIn && !userIdStr.isNullOrEmpty() && userIdStr != "null") {
            userIdStr.toIntOrNull()
        } else {
            null
        }
    }

    private fun redirectToLogin(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        startActivity(Intent(this, LoginPage::class.java))
    }

    private fun launchSearchResults(key: String, value: String) {
        val intent = Intent(this, SearchResults::class.java)
        intent.putExtra(key, value)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commitAllowingStateLoss()
    }

    private fun setupQuickSearches() {
        findViewById<Button>(R.id.btn1).setOnClickListener {
            launchSearchResults("Model", "2020")
        }

        findViewById<Button>(R.id.btn2).setOnClickListener {
            launchSearchResults("Model", "2024")
        }

        findViewById<LinearLayout>(R.id.firstColumn).setOnClickListener {
            launchSearchResults("TRANSMISSION_TYPE", "automatic")
        }

        findViewById<LinearLayout>(R.id.secondColumn).setOnClickListener {
            launchSearchResults("TRANSMISSION_TYPE", "manual")
        }

        findViewById<LinearLayout>(R.id.thirdColumn).setOnClickListener {
            launchSearchResults("Fuel_TYPE", "Electric")
        }

        findViewById<LinearLayout>(R.id.fourthColumn).setOnClickListener {
            launchSearchResults("Body_TYPE", "Imported")
        }

        findViewById<LinearLayout>(R.id.firstColumn2).setOnClickListener {
            launchSearchResults("Body_TYPE", "Japanese")
        }

        findViewById<LinearLayout>(R.id.secondColumn2).setOnClickListener {
            launchSearchResults("Engine_Capacity", "660")
        }

        findViewById<LinearLayout>(R.id.thirdColumn2).setOnClickListener {
            launchSearchResults("Engine_Capacity", "1300")
        }

        findViewById<LinearLayout>(R.id.fourthColumn2).setOnClickListener {
            launchSearchResults("Body_TYPE", "Sports")
        }
    }
}
