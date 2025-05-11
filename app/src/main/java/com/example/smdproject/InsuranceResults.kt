package com.example.smdproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class InsuranceResults : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var resultsContainer: LinearLayout
    private lateinit var noResultsView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var carModelTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.insurance_results)

        // Initialize views
        progressBar = findViewById(R.id.progressBar)
        resultsContainer = findViewById(R.id.resultsContainer)
        noResultsView = findViewById(R.id.noResultsView)
        recyclerView = findViewById(R.id.insuranceRecyclerView)
        carModelTitle = findViewById(R.id.carModelTitle)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)
        // Set OnClickListener to the arrow_back icon
        arrowBack.setOnClickListener {
            finish() // Use finish to go back
        }

        // Get data from intent
        val carModel = intent.getStringExtra("carModel") ?: ""
        val carPrice = intent.getStringExtra("carPrice") ?: ""

        // Update the title with car model
        if (carModel.isNotEmpty()) {
            carModelTitle.text = "Insurance for $carModel"
        }

        // Setup the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Show loading
        showLoading(true)

        // Fetch insurance data
        fetchInsuranceData(carModel, carPrice)
    }

    private fun fetchInsuranceData(model: String, price: String) {
        thread {
            try {
                // Change this to your actual API URL
                val apiUrl = "http://192.168.18.111/server/get_insurance.php"

                // Open connection
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection

                // Set up the POST request
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                // Create POST data
                val postData = "model=$model&price=$price"

                // Send POST data
                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(postData)
                outputStream.flush()
                outputStream.close()

                // Connect to server
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("InsuranceResults", "Response code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("InsuranceResults", "API Response: $response")

                    runOnUiThread {
                        showLoading(false)

                        try {
                            // Parse JSON response
                            val jsonResponse = JSONObject(response)
                            val status = jsonResponse.getString("status")

                            if (status == "success") {
                                val insuranceArray = jsonResponse.getJSONArray("insurance_info")
                                if (insuranceArray.length() > 0) {
                                    // Create list of insurance items
                                    val insuranceList = mutableListOf<InsuranceItem>()

                                    for (i in 0 until insuranceArray.length()) {
                                        val item = insuranceArray.getJSONObject(i)
                                        insuranceList.add(
                                            InsuranceItem(
                                                companyName = item.getString("insurance_company"),
                                                insuranceType = item.getString("insurance_type"),
                                                coverageDetails = item.getString("coverage_details"),
                                                annualPremium = item.getString("annual_premium")
                                            )
                                        )
                                    }

                                    // Update UI with insurance data
                                    recyclerView.adapter = InsuranceAdapter(insuranceList)
                                    resultsContainer.visibility = View.VISIBLE
                                    noResultsView.visibility = View.GONE

                                } else {
                                    // No results found
                                    resultsContainer.visibility = View.GONE
                                    noResultsView.visibility = View.VISIBLE
                                }
                            } else {
                                // Handle error from API
                                val error = jsonResponse.optString("error", "Unknown error")
                                Toast.makeText(this@InsuranceResults, error, Toast.LENGTH_LONG).show()
                                loadMockData() // Load mock data as fallback
                            }
                        } catch (e: Exception) {
                            Log.e("InsuranceResults", "JSON parsing error", e)
                            Toast.makeText(
                                this@InsuranceResults,
                                "Error processing data: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()

                            // Load mock data as fallback
                            loadMockData()
                        }
                    }
                } else {
                    Log.e("InsuranceResults", "HTTP Error: $responseCode")
                    runOnUiThread {
                        showLoading(false)
                        Toast.makeText(
                            this@InsuranceResults,
                            "Server error: $responseCode",
                            Toast.LENGTH_LONG
                        ).show()

                        // Load mock data as fallback
                        loadMockData()
                    }
                }
            } catch (e: Exception) {
                Log.e("InsuranceResults", "Network error", e)
                runOnUiThread {
                    showLoading(false)
                    Toast.makeText(
                        this@InsuranceResults,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Load mock data as fallback
                    loadMockData()
                }
            }
        }
    }

    private fun loadMockData() {
        // Mock data for testing
        val mockInsuranceList = listOf(
            InsuranceItem(
                companyName = "Shaheen Insurance",
                insuranceType = "Comprehensive",
                coverageDetails = "Full coverage with roadside assistance",
                annualPremium = "Rs. 29,000"
            ),
            InsuranceItem(
                companyName = "Pak Turk Insurance",
                insuranceType = "Third Party",
                coverageDetails = "Basic coverage for third party damage",
                annualPremium = "Rs. 15,000"
            ),
            InsuranceItem(
                companyName = "EFU Insurance",
                insuranceType = "Premium",
                coverageDetails = "Full coverage with additional benefits",
                annualPremium = "Rs. 35,000"
            ),
            InsuranceItem(
                companyName = "Adamjee Insurance",
                insuranceType = "Standard",
                coverageDetails = "Standard coverage with theft protection",
                annualPremium = "Rs. 27,500"
            )
        )

        // Display mock data
        recyclerView.adapter = InsuranceAdapter(mockInsuranceList)
        resultsContainer.visibility = View.VISIBLE
        noResultsView.visibility = View.GONE

        Toast.makeText(
            this@InsuranceResults,
            "Showing sample data (API connection issue)",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        resultsContainer.visibility = View.GONE
        noResultsView.visibility = View.GONE
    }
}