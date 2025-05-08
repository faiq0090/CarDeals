package com.example.smdproject

data class Dealership(
    val brand: String,
    val city: String,
    val location: String,
    val phone: String = "",  // Optional fields with defaults
    val logoResource: Int = 0
)