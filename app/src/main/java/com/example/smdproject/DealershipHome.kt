package com.example.smdproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class DealershipHome: AppCompatActivity() {

    // Define API endpoint
    private val API_URL = "http://192.168.18.177/server/get_dealership.php"

    private lateinit var brandEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var findButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dealership_home_page)

        // Initialize UI components
        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)
        brandEditText = findViewById(R.id.editTextText)
        locationEditText = findViewById(R.id.LocationText)
        findButton = findViewById(R.id.button3)

        // Set OnClickListener for back arrow to return to home screen
        arrowBack.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }

        // Set OnClickListener for find button to search dealerships
        findButton.setOnClickListener {
            searchDealerships()
        }
    }

    private fun searchDealerships() {
        val brand = brandEditText.text.toString().trim()
        val city = locationEditText.text.toString().trim()

        // Validate input
        if (brand.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please enter both brand and city", Toast.LENGTH_SHORT).show()
            return
        }

        // Create request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create POST request
        val stringRequest = object : StringRequest(
            Request.Method.POST, API_URL,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        // Pass data to results screen
                        val intent = Intent(this, DealershipResults::class.java)
                        intent.putExtra("brand", brand)
                        intent.putExtra("city", city)
                        intent.putExtra("response", response)
                        startActivity(intent)
                    } else {
                        // Show error message
                        val message = jsonResponse.getString("message")
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Network error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            // Add POST parameters
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["brand"] = brand
                params["city"] = city
                return params
            }
        }

        // Add request to queue
        requestQueue.add(stringRequest)
    }
}