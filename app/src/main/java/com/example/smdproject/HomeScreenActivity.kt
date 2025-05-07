package com.example.smdproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import sellcar

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentUserId: Int? = null
    private var isLoggedIn: Boolean = false

    override fun onStart() {
        super.onStart()
        loadUserSession()
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
