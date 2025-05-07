package com.example.smdproject

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

// Car data class for the adapter
data class Car(
    val id: Int,
    val userId: Int,
    val carType: String,
    val carModel: String,
    val city: String,
    val model: String,
    val registered: String,
    val color: String,
    val fuelType: String,
    val bodyType: String,
    val transmissionType: String,
    val engineCapacity: String,
    val kmDriven: String,
    val price: String,
    val description: String,
    val address: String,
    val imageBase64: String
)

class CarAdapter(
    private val context: Context,
    private val carList: List<Car>,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.car_item, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        holder.bind(car)
        holder.itemView.setOnClickListener { onItemClick(car) }
    }

    override fun getItemCount(): Int = carList.size

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        private val tvCarTitle: TextView = itemView.findViewById(R.id.tvCarTitle)
        private val tvCarPrice: TextView = itemView.findViewById(R.id.tvCarPrice)
        private val tvCarCity: TextView = itemView.findViewById(R.id.tvCarCity)
        private val tvCarYear: TextView = itemView.findViewById(R.id.tvCarYear)
        private val tvCarKm: TextView = itemView.findViewById(R.id.tvCarKm)
        private val tvCarFuel: TextView = itemView.findViewById(R.id.tvCarFuel)
        private val tvCarTransmission: TextView = itemView.findViewById(R.id.tvCarTransmission)

        fun bind(car: Car) {
            // Set car image
            if (car.imageBase64.isNotEmpty()) {
                try {
                    val decodedBytes = Base64.decode(car.imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ivCarImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    ivCarImage.setImageResource(R.drawable.car_06)
                }
            } else {
                ivCarImage.setImageResource(R.drawable.car_06)
            }

            // Format car title (Make + Model)
            tvCarTitle.text = "${car.carModel} ${car.carType}"

            // Format price with currency
            try {
                val priceValue = car.price.toLong()
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("en", "PK"))
                    .format(priceValue)
                tvCarPrice.text = formattedPrice
            } catch (e: Exception) {
                tvCarPrice.text = "Rs. ${car.price}"
            }

            // Set other car details
            tvCarCity.text = car.city
            tvCarYear.text = car.model
            tvCarKm.text = "${car.kmDriven} km"
            tvCarFuel.text = car.fuelType
            tvCarTransmission.text = car.transmissionType
        }
    }
}