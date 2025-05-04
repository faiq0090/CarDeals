package com.example.smdproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    // Splash screen timeout in milliseconds
    private val SPLASH_TIME_OUT: Long = 2000 // 3 seconds
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            // Handler to navigate to the main activity after the splash screen timeout
            Handler().postDelayed({
                // Start your main activity
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)

                // Close this activity
                finish()
            }, SPLASH_TIME_OUT)

        /*
        val loginbutton = findViewById<Button>(R.id.loginbutton)
        // Set OnClickListener to the arrow_back9 icon
        loginbutton.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }


        val Signupbutton = findViewById<TextView>(R.id.textView5)
        // Set OnClickListener to the arrow_back9 icon
        Signupbutton.setOnClickListener {
            startActivity(Intent(this, SignupPage::class.java))
        }

        val searchView = findViewById<SearchView>(R.id.searchview1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Start the SearchResults Activity regardless of the query
                startActivity(Intent(this@MainActivity, SearchResults::class.java))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search text change here
                return false
            }
        })

        val Insurancepage = findViewById<Button>(R.id.Insurancebtn)
        Insurancepage.setOnClickListener {
            startActivity(Intent(this, InsuranceHome::class.java))
        }
        val Dealershipbtn = findViewById<Button>(R.id.Dealershipbtn)
        Dealershipbtn.setOnClickListener {
            startActivity(Intent(this, DealershipHome::class.java))
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.action_chat -> {
                    replaceFragment(AllChats())
                    true
                }
                R.id.action_upload -> {
                    replaceFragment(sellcar())
                    true
                }
                R.id.action_myads ->{
                    replaceFragment(Myads())
                    true
                }
                R.id.action_profile->{
                    replaceFragment(profile())
                    true
                }
                R.id.action_home->{
                    // Finish and restart the activity to reload the same activity
                    finish()
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    true
                }

                else -> {false}
            }
        }*/

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commitAllowingStateLoss()
    }
}