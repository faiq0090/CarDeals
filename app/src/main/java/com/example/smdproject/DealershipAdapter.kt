package com.example.smdproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DealershipAdapter(
    private val dealerships: List<Dealership>,
    private val onItemClick: (Dealership) -> Unit
) : RecyclerView.Adapter<DealershipAdapter.DealershipViewHolder>() {

    class DealershipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dealershipName: TextView = view.findViewById(R.id.textViewDealershipName)
        val dealershipLocation: TextView = view.findViewById(R.id.textViewLocation)
        val dealershipPhone: TextView = view.findViewById(R.id.textViewPhone)
        val dealershipLogo: ImageView = view.findViewById(R.id.imageViewLogo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealershipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dealership, parent, false)
        return DealershipViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealershipViewHolder, position: Int) {
        val dealership = dealerships[position]

        holder.dealershipName.text = "${dealership.brand} ${dealership.location}"
        holder.dealershipLocation.text = "${dealership.location}, ${dealership.city}"
        holder.dealershipPhone.text = dealership.phone

        // Set logo if available
        if (dealership.logoResource != 0) {
            holder.dealershipLogo.setImageResource(dealership.logoResource)
        } else {
            // Default logo for Toyota
            holder.dealershipLogo.setImageResource(R.drawable.tlog)
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(dealership)
        }
    }

    override fun getItemCount() = dealerships.size
}