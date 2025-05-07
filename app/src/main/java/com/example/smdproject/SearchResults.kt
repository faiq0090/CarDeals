package com.example.smdproject

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smdproject.db.CarDatabaseHelper

class SearchResults : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvSearchTerm: TextView
    private lateinit var tvResultsCount: TextView
    private lateinit var dbHelper: CarDatabaseHelper
    private val TAG = "SearchResults"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        tvSearchTerm = findViewById(R.id.tvSearchTerm)
        tvResultsCount = findViewById(R.id.tvResultsCount)
        // Setup back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Initialize DB helper
        dbHelper = CarDatabaseHelper(this)

        // Get search parameters from intent
        val extras = intent.extras
        if (extras != null) {
            // Determine which key-value pair was passed
            val keySet = extras.keySet()
            if (keySet.isNotEmpty()) {
                // Get the first key (assuming only one key-value pair is passed)
                val key = keySet.first()
                val value = extras.getString(key, "")

                // Display the search term
                tvSearchTerm.text = value

                // Fetch filtered cars from database
                val filteredCars = getFilteredCars(key, value)

                // Update results count
                tvResultsCount.text = filteredCars.size.toString()

                // Set up the RecyclerView
                setupRecyclerView(filteredCars)

                Log.d(TAG, "Filtering by $key = $value, found ${filteredCars.size} results")
            } else {
                Log.e(TAG, "No extras found in intent")
                tvSearchTerm.text = "No search term"
                setupRecyclerView(emptyList())
            }
        } else {
            Log.e(TAG, "Intent extras is null")
            tvSearchTerm.text = "No search term"
            setupRecyclerView(emptyList())
        }
    }

    private fun getFilteredCars(key: String, value: String): List<ContentValues> {
        return when (key.lowercase()) {
            "model" -> {
                // Filter by model year
                dbHelper.getAllCars().filter { cv ->
                    val model = cv.getAsString("model") ?: ""
                    model == value
                }
            }
            "transmission_type" -> {
                if (value.equals("automatic", ignoreCase = true)) {
                    dbHelper.getAutomaticCars()
                } else if (value.equals("manual", ignoreCase = true)) {
                    dbHelper.getManualCars()
                } else {
                    emptyList()
                }
            }
            "fuel_type" -> {
                // Filter by fuel type
                dbHelper.getAllCars().filter { cv ->
                    val fuelType = cv.getAsString("fuel_type") ?: ""
                    fuelType.equals(value, ignoreCase = true)
                }
            }
            "body_type" -> {
                when (value.lowercase()) {
                    "imported" -> dbHelper.getImportedCars()
                    "japanese" -> dbHelper.getJapaneseCars()
                    "sports" -> dbHelper.getSportsCars()
                    else -> {
                        // Generic body type filter
                        dbHelper.getAllCars().filter { cv ->
                            val bodyType = cv.getAsString("body_type") ?: ""
                            bodyType.equals(value, ignoreCase = true)
                        }
                    }
                }
            }
            "engine_capacity" -> {
                when (value) {
                    "660" -> dbHelper.get660ccCars()
                    "1300" -> dbHelper.get1300ccCars()
                    else -> {
                        // Generic engine capacity filter
                        dbHelper.getAllCars().filter { cv ->
                            val engineCapacity = cv.getAsString("engine_capacity") ?: ""
                            engineCapacity == value
                        }
                    }
                }
            }
            else -> {
                // Default: search in all text fields
                val searchValue = value.lowercase()
                dbHelper.getAllCars().filter { cv ->
                    cv.valueSet().any { entry ->
                        val fieldValue = entry.value?.toString()?.lowercase() ?: ""
                        fieldValue.contains(searchValue)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(carList: List<ContentValues>) {
        // Convert ContentValues to Car objects
        val cars = carList.map { cv ->
            Car(
                id = cv.getAsInteger("id") ?: 0,
                userId = cv.getAsInteger("user_id") ?: 0,
                carType = cv.getAsString("car_type") ?: "",
                carModel = cv.getAsString("car_model") ?: "",
                city = cv.getAsString("city") ?: "",
                model = cv.getAsString("model") ?: "",
                registered = cv.getAsString("registered") ?: "",
                color = cv.getAsString("color") ?: "",
                fuelType = cv.getAsString("fuel_type") ?: "",
                bodyType = cv.getAsString("body_type") ?: "",
                transmissionType = cv.getAsString("transmission_type") ?: "",
                engineCapacity = cv.getAsString("engine_capacity") ?: "",
                kmDriven = cv.getAsString("km_driven") ?: "",
                price = cv.getAsString("price") ?: "",
                description = cv.getAsString("description") ?: "",
                address = cv.getAsString("address") ?: "",
                imageBase64 = cv.getAsString("image_base64") ?: ""
            )
        }

        // Setup adapter and layout manager
        val adapter = CarAdapter(this, cars) { car ->
            // Handle car item click (optional)
//            val intent = Intent(this, CarDetailActivity::class.java)
//            intent.putExtra("CAR_ID", car.id)
//            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }
}