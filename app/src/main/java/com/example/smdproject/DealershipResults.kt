package com.example.smdproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject

class DealershipResults: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var searchInfoTextView: TextView
    private lateinit var resultCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dealership_results)

        // Initialize UI components
        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)
        recyclerView = findViewById(R.id.recyclerViewDealerships)
        noResultsLayout = findViewById(R.id.layoutNoResults)
        searchInfoTextView = findViewById(R.id.textViewSearchInfo)
        resultCountTextView = findViewById(R.id.textViewResultCount)
        val tryAgainButton = findViewById<Button>(R.id.buttonTryAgain)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set OnClickListener for back arrow
        arrowBack.setOnClickListener {
            onBackPressed()
        }

        // Set OnClickListener for try again button
        tryAgainButton.setOnClickListener {
            onBackPressed()
        }

        // Get data from intent
        val brand = intent.getStringExtra("brand") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val response = intent.getStringExtra("response")

        // Update search info text
        searchInfoTextView.text = "Showing results for $brand in $city"

        // Process the API response
        if (response != null) {
            processApiResponse(response, brand, city)
        } else {
            // Show error and no results
            showNoResults()
            Toast.makeText(this, "Error: No data received", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processApiResponse(response: String, brand: String, city: String) {
        try {
            val jsonResponse = JSONObject(response)
            val status = jsonResponse.getString("status")

            if (status == "success") {
                val locationsArray = jsonResponse.getJSONArray("locations")
                val dealerships = ArrayList<Dealership>()

                // Create dummy phone numbers (in a real app, this would come from the API)
                val phoneNumbers = arrayOf("061-1234567", "051-9876543", "042-3456789")

                // Parse locations and create dealership objects
                for (i in 0 until locationsArray.length()) {
                    val location = locationsArray.getString(i)
                    val phone = if (i < phoneNumbers.size) phoneNumbers[i] else "N/A"

                    dealerships.add(
                        Dealership(
                            brand = brand,
                            city = city,
                            location = location,
                            phone = phone,
                            logoResource = R.drawable.tlog // Assuming you have this drawable
                        )
                    )
                }

                // Update result count text
                resultCountTextView.text = "${dealerships.size} dealerships found"

                // Check if we have results
                if (dealerships.isEmpty()) {
                    showNoResults()
                } else {
                    showResults()

                    // Set up adapter
                    val adapter = DealershipAdapter(dealerships) { dealership ->
                        // Handle dealership item click
                        Toast.makeText(
                            this,
                            "Selected: ${dealership.brand} at ${dealership.location}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Here you could navigate to a dealership detail screen
                        // val intent = Intent(this, DealershipDetailActivity::class.java)
                        // intent.putExtra("location", dealership.location)
                        // startActivity(intent)
                    }

                    recyclerView.adapter = adapter
                }
            } else {
                // Show error message and no results
                val message = jsonResponse.getString("message")
                showNoResults()
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: JSONException) {
            // Show error message and no results
            showNoResults()
            Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showResults() {
        recyclerView.visibility = View.VISIBLE
        noResultsLayout.visibility = View.GONE
    }

    private fun showNoResults() {
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}