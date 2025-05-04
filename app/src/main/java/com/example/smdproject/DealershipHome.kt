package com.example.smdproject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout

class DealershipHome: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dealership_home_page)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back22)
        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }
        val SearchresultsBtn = findViewById<Button>(R.id.button3)
        // Set OnClickListener to the arrow_back9 icon
        SearchresultsBtn.setOnClickListener {
            startActivity(Intent(this, DealershipResults::class.java))
        }

    }
}