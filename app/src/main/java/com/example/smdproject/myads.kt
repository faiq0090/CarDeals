package com.example.smdproject

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Base64
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import com.example.smdproject.db.CarDatabaseHelper  // Adjust if your DBHelper is in a different package

class Myads : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: MyAdsAdapter
    private lateinit var dbHelper: CarDatabaseHelper
    private var userId: String? = null
    private var myAdsList: List<ContentValues> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_myads, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val sharedPref = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPref.getString("user_id", null)

        dbHelper = CarDatabaseHelper(requireContext())

        val allAds = dbHelper.getAllCars()
        myAdsList = allAds.filter { it.getAsString("user_id") == userId }

        carAdapter = MyAdsAdapter(myAdsList)
        recyclerView.adapter = carAdapter

        return view
    }
}
