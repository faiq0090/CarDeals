package com.example.smdproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = null
    //private var currentUser: FirebaseUser? = mAuth.currentUser

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        currentUser = firebaseAuth.currentUser
        Log.d("Authentication", "Current user: $currentUser")
    }

    override fun onStart() {
        super.onStart()
        // mAuth.signOut() // Sign out any previously logged-in user
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(mAuthListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)
        val searchView = findViewById<SearchView>(R.id.searchview1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Start the SearchResults Activity and pass the search query
                val intent = Intent(this@HomeScreenActivity, SearchResults::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text change here
                return false
            }
        })
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Model", "2020")
            startActivity(intent)
        }

        val btn2 = findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Model", "2024")
            startActivity(intent)
        }
        val firstColumn = findViewById<RelativeLayout>(R.id.firstColumn)
        firstColumn.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("TRANSMISSION_TYPE", "automatic")
            startActivity(intent)
        }
        val secondColumn = findViewById<RelativeLayout>(R.id.secondColumn)
        secondColumn.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("TRANSMISSION_TYPE", "manual")
            startActivity(intent)
        }

        val thirdColumn = findViewById<RelativeLayout>(R.id.thirdColumn)
        thirdColumn.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Fuel_TYPE", "Electric")
            startActivity(intent)
        }

        val  fourthColumn = findViewById<RelativeLayout>(R.id. fourthColumn)
        fourthColumn.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Body_TYPE", "Imported")
            startActivity(intent)
        }


        val  firstColumn2 = findViewById<RelativeLayout>(R.id. firstColumn2)
        firstColumn2.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Body_TYPE", "Japanese")
            startActivity(intent)
        }
        val  secondColumn2 = findViewById<RelativeLayout>(R.id. secondColumn2)
        secondColumn2.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Engine_Capacity", "660")
            startActivity(intent)
        }
        val  thirdColumn2 = findViewById<RelativeLayout>(R.id.thirdColumn2)
        thirdColumn2.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Engine_Capacity", "1300")
            startActivity(intent)
        }
        val  fourthColumn2 = findViewById<RelativeLayout>(R.id.fourthColumn2)
        fourthColumn2.setOnClickListener {
            val intent = Intent(this, SearchResults::class.java)
            intent.putExtra("Body_TYPE", "Sports")
            startActivity(intent)
        }
        val Insurancepage = findViewById<Button>(R.id.Insurancebtn)
        Insurancepage.setOnClickListener {
            startActivity(Intent(this, InsuranceHome::class.java))
        }
        val Dealershipbtn = findViewById<Button>(R.id.Dealershipbtn)
        Dealershipbtn.setOnClickListener {
            startActivity(Intent(this, DealershipHome::class.java))
        }

        mAuth.addAuthStateListener(mAuthListener)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.action_chat -> {
                    if (currentUser != null) {
                        // User is authenticated, allow access
                        Log.d("Authentication", "User is authenticated. Allowing access to sellcar fragment.")
                        replaceFragment(AllChats())
                    } else {
                        // User is not authenticated (guest user), redirect to login screen
                        Toast.makeText(this, "Log in to chat.", Toast.LENGTH_LONG)
                            .show()
                        Log.d("Authentication", "User is not authenticated. Redirecting to login screen.")
                        val intent = Intent(this@HomeScreenActivity, LoginPage::class.java)
                        startActivity(intent)
                    }
                    true // This line was missing in your original code
                }
                R.id.action_upload -> {
                    if (currentUser != null) {
                        // User is authenticated, allow access
                        Log.d("Authentication", "User is authenticated. Allowing access to sellcar fragment.")
                        replaceFragment(sellcar())
                    } else {
                        // User is not authenticated (guest user), redirect to login screen
                        Toast.makeText(this, "Log in to post ads.", Toast.LENGTH_LONG)
                            .show()
                        Log.d("Authentication", "User is not authenticated. Redirecting to login screen.")
                        val intent = Intent(this@HomeScreenActivity, LoginPage::class.java)
                        startActivity(intent)
                    }
                    true // This line was missing in your original code
                }
                R.id.action_myads -> {
                    if (currentUser != null) {
                        // User is authenticated, allow access
                        Log.d("Authentication", "User is authenticated. Allowing access to sellcar fragment.")
                        replaceFragment(Myads())
                    } else {
                        // User is not authenticated (guest user), redirect to login screen
                        Toast.makeText(this, "Log in to view your ads.", Toast.LENGTH_LONG)
                            .show()
                        Log.d("Authentication", "User is not authenticated. Redirecting to login screen.")
                        val intent = Intent(this@HomeScreenActivity, LoginPage::class.java)
                        startActivity(intent)
                    }
                    true // This line was missing in your original code
                }
                R.id.action_profile -> {
                    replaceFragment(profile())
                    true
                }
                R.id.action_home -> {
                    // Finish and restart the activity to reload the same activity
                    finish()
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }
                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commitAllowingStateLoss()
    }
}