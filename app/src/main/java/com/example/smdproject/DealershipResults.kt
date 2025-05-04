package com.example.smdproject
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DealershipResults: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dealership_results)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)
        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            startActivity(Intent(this, DealershipHome::class.java))
        }

    }
}