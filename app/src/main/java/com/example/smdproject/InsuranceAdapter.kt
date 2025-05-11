package com.example.smdproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class InsuranceAdapter(private val insuranceList: List<InsuranceItem>) :
    RecyclerView.Adapter<InsuranceAdapter.InsuranceViewHolder>() {

    class InsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.companyName)
        val rateValue: TextView = itemView.findViewById(R.id.rateValue)
        val totalValue: TextView = itemView.findViewById(R.id.totalValue)
        val btnApply: Button = itemView.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.insurance_item, parent, false)
        return InsuranceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {
        val currentItem = insuranceList[position]

        // Set the company name
        holder.companyName.text = currentItem.companyName

        // Set the rate value (insurance type as rate)
        holder.rateValue.text = currentItem.insuranceType

        // Set the total value (annual premium)
        holder.totalValue.text = "${currentItem.annualPremium}/year"

        // Set click listener for apply button
        holder.btnApply.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Applied for ${currentItem.companyName} insurance",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }
}