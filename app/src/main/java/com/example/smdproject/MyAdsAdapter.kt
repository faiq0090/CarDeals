package com.example.smdproject

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdsAdapter(private val adList: List<ContentValues>) : RecyclerView.Adapter<MyAdsAdapter.AdViewHolder>() {

    class AdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carImage: ImageView = itemView.findViewById(R.id.carImage)
        val carTitle: TextView = itemView.findViewById(R.id.carTitle)
        val carPrice: TextView = itemView.findViewById(R.id.carPrice)
        val showDetailsButton: TextView = itemView.findViewById(R.id.showDetailsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_ad, parent, false)
        return AdViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = adList[position]
        holder.carTitle.text = ad.getAsString("car_model") + " - " + ad.getAsString("city")
        holder.carPrice.text = "Rs. " + ad.getAsString("price")

        val imageBase64 = ad.getAsString("image")
        if (!imageBase64.isNullOrEmpty()) {
            val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            holder.carImage.setImageBitmap(bitmap)
        }

        // Handle Show More Details button click
        holder.showDetailsButton.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = adList.size
}
