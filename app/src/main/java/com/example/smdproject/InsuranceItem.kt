package com.example.smdproject

// Data class for insurance items from the API
data class InsuranceItem(
    val companyName: String,
    val insuranceType: String,
    val coverageDetails: String,
    val annualPremium: String
)