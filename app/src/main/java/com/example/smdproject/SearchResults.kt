package com.example.smdproject
import Car
import CarAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.database.*

class SearchResults : AppCompatActivity() {

    private lateinit var carAdapter: CarAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var searchText: TextView  // Add TextView variable
    private lateinit var numText: TextView
    private var transmission: String? = null
    private var fueltype: String? = null
    private var bodytype: String? = null
    private var EngineCapacity : String? = null
    private var modelyear : String? = null

    private var query: String? = null
    private var isSearchQuery: Boolean = false

    private lateinit var carDatabaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)

        carDatabaseHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        query = intent.getStringExtra("QUERY")
        Log.d("SearchQuery", "Search Query: $query") // Log the search query
        isSearchQuery = query != null

        // Receive intent
        modelyear = intent.getStringExtra("Model")

        // Get transmission type from intent
        transmission = intent.getStringExtra("TRANSMISSION_TYPE")
        fueltype = intent.getStringExtra("Fuel_TYPE")
        Log.d("FuelType", "FuelType: $fueltype") // Log the search query

        bodytype = intent.getStringExtra("Body_TYPE")

        EngineCapacity = intent.getStringExtra("Engine_Capacity")


        // Display search query in TextView
        //searchText = findViewById(R.id.Searchtext)
        // searchText.text = "$query"

        // Display search query in TextView
        searchText = findViewById(R.id.Searchtext)
        val searchValue = modelyear ?: query ?: transmission ?: fueltype ?: bodytype ?: EngineCapacity ?: ""
        searchText.text = searchValue


        numText = findViewById(R.id.numtext) // Initialize TextView
        numText.text = "" // Set initial text

        if (isInternetAvailable()) {
            fetchCarsFromFirebase()
        } else {
            fetchCarsFromSQLite()
        }

        val arrowBack = findViewById<ImageView>(R.id.arrow_back12)
        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            startActivity(Intent(this, HomeScreenActivity::class.java))
        }
    }

    private fun fetchCarsFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("Car_Ads")

        val carList: MutableList<Car> = mutableListOf()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                carList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val city = postSnapshot.child("city").getValue(String::class.java)
                    val model = postSnapshot.child("model").getValue(String::class.java)
                    val registered = postSnapshot.child("registered").getValue(String::class.java)
                    val color = postSnapshot.child("color").getValue(String::class.java)
                    val km = postSnapshot.child("km").getValue(String::class.java)
                    val FuelType = postSnapshot.child("FuelType").getValue(String::class.java)
                    val price = postSnapshot.child("price").getValue(String::class.java)
                    val desc = postSnapshot.child("desc").getValue(String::class.java)
                    val carCompany = postSnapshot.child("carCompany").getValue(String::class.java)
                    val carModel = postSnapshot.child("carModel").getValue(String::class.java)
                    val transmissionType = postSnapshot.child("transmissionType").getValue(String::class.java)
                    val bodyType = postSnapshot.child("bodyType").getValue(String::class.java)
                    val address = postSnapshot.child("address").getValue(String::class.java)
                    val engineCapacity = postSnapshot.child("EngineCapacity").getValue(String::class.java)
                    val userId = postSnapshot.child("userId").getValue(String::class.java)
                    val carImg = postSnapshot.child("imageUrl").getValue(String::class.java) // Fetch image URL

                    if ((isSearchQuery && carModel?.contains(query!!, ignoreCase = true) == true) ||
                        (!isSearchQuery && transmissionType?.equals(transmission, ignoreCase = true) == true) ||
                        (!isSearchQuery && FuelType?.equals(fueltype, ignoreCase = true) == true) ||
                        (!isSearchQuery && bodyType?.equals(bodytype, ignoreCase = true) == true) ||
                        (!isSearchQuery && model?.equals(modelyear, ignoreCase = true) == true) ||
                        (!isSearchQuery && engineCapacity?.equals(EngineCapacity, ignoreCase = true) == true)) {
                        val car = Car(city, model, registered, color, km, FuelType, price, desc, carCompany, carModel, transmissionType, bodyType, address, engineCapacity, userId, carImg)
                        carList.add(car)
                        Log.d("CarInfo", "Car(city=$city, model=$model, registered=$registered, color=$color, km=$km, fuelType=$FuelType, price=$price, desc=$desc, carCompany=$carCompany, carModel=$carModel, transmissionType=$transmissionType, bodyType=$bodyType, address=$address, engineCapacity=$engineCapacity, userId=$userId, imageUrl=$carImg)")
                    }
                }
                numText.text = "${carList.size}" // Update number of search results

                carAdapter.notifyDataSetChanged() // Notify adapter about the change
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })

        carAdapter = CarAdapter(carList)
        recyclerView.adapter = carAdapter
    }

    private fun fetchCarsFromSQLite() {
        val carList = carDatabaseHelper.getCars()

        carAdapter = CarAdapter(carList)
        recyclerView.adapter = carAdapter

        numText.text = "${carList.size}"
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
