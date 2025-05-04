package com.example.smdproject

import Car
import CarAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.smdproject.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*

class Myads : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var carAdapter: CarAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_myads, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val auth: FirebaseAuth = Firebase.auth
        userId = auth.currentUser?.uid ?: ""

        databaseRef = FirebaseDatabase.getInstance().getReference("Car_Ads")

        val carList: MutableList<Car> = mutableListOf()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                carList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val car = postSnapshot.getValue(Car::class.java)
                    if (car != null && car.userId == userId) {
                        carList.add(car)
                    }
                }
                carAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                // ...
            }
        })

        carAdapter = CarAdapter(carList)
        recyclerView.adapter = carAdapter

        return view
    }
}