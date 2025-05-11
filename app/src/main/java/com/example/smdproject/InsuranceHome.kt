package com.example.smdproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class InsuranceHome: AppCompatActivity() {
    private lateinit var carModelInput: EditText
    private lateinit var carPriceInput: EditText
    private lateinit var searchInsuranceBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.insurance_home_page)

        // Initialize UI components
        carModelInput = findViewById(R.id.editTextText)
        carPriceInput = findViewById(R.id.editTextNumber)
        searchInsuranceBtn = findViewById(R.id.button3)
        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)

        // Set OnClickListener to the arrow_back icon
        arrowBack.setOnClickListener {
            finish() // Use finish() to go back
        }

        // Set OnClickListener to the search button
        searchInsuranceBtn.setOnClickListener {
            submitForm()
        }
    }

    private fun submitForm() {
        val carModel = carModelInput.text.toString().trim()
        val carPrice = carPriceInput.text.toString().trim()

        // Input validation
        if (carModel.isEmpty()) {
            carModelInput.error = "Please enter a car model"
            return
        }

        if (carPrice.isEmpty()) {
            carPriceInput.error = "Please enter car price"
            return
        }

        try {
            // Validate that price is a number
            val priceValue = carPrice.toDouble()
            if (priceValue <= 0) {
                carPriceInput.error = "Please enter a valid price"
                return
            }

            // Create intent and add data
            val intent = Intent(this, InsuranceResults::class.java).apply {
                putExtra("carModel", carModel)
                putExtra("carPrice", carPrice)
            }
            startActivity(intent)

        } catch (e: NumberFormatException) {
            carPriceInput.error = "Please enter a valid price"
        }
    }
}