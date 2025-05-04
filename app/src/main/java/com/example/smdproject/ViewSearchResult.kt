package com.example.smdproject
import Car
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.squareup.picasso.Picasso

private const val PERMISSION_CODE_CALL_PHONE = 123 // Use any integer value



class ViewSearchResult: AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "viewsearchresult_channel"
        private const val NOTIFICATION_ID = 124
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_search_result)
        val car = intent.getParcelableExtra<Car>("car")
        car?.let {
            // Display car details
            println("Car details: $it")
        }
        car?.let {
            // Display car details
            val carNameTextView = findViewById<TextView>(R.id.CarName)
            val carPriceTextView = findViewById<TextView>(R.id.price)
            val carLocationTextView = findViewById<TextView>(R.id.locationtext1)
            val carModelTextView = findViewById<TextView>(R.id.modeltext)
            val carMileageTextView = findViewById<TextView>(R.id.mileagetext)
            val carFuelTypeTextView = findViewById<TextView>(R.id.FuelTypetext)
            val carRegisteredTextView = findViewById<TextView>(R.id.registeredtext)
            val carTransmissionTextView = findViewById<TextView>(R.id.transmissiontext)
            val carColorTextView = findViewById<TextView>(R.id.colortext)
            val carEngineCapacityTextView = findViewById<TextView>(R.id.EngineCapacityText)
            val carBodyTypeTextView = findViewById<TextView>(R.id.BodyTypeText)
            val CarImgView = findViewById<ImageView>(R.id.CarImg)

            carNameTextView.text = "${it.carCompany} ${it.carModel}"
            carPriceTextView.text = it.price
            carLocationTextView.text = "${it.address}, ${it.city}"
            carModelTextView.text = it.model
            carMileageTextView.text = it.km
            carFuelTypeTextView.text = it.fuelType
            carTransmissionTextView.text=it.transmissionType
            carRegisteredTextView.text = it.registered
            carColorTextView.text = it.color
            carEngineCapacityTextView.text = it.engineCapacity
            carBodyTypeTextView.text = it.bodyType

            // Load car image using Picasso
            it.imageUrl?.let { url ->
                Picasso.get().load(url)
                    .placeholder(R.drawable.img) // Placeholder image
                    .error(R.drawable.img) // Error image
                    .into(CarImgView)
            }
        }
        val randomAdID = (10000..200000).random()
        val adIDTextView = findViewById<TextView>(R.id.AdID)
        adIDTextView.text = randomAdID.toString()

        val arrowBack = findViewById<ImageView>(R.id.arrow_back12)
        // Set OnClickListener to the arrow_back9 icon
        arrowBack.setOnClickListener {
            startActivity(Intent(this, SearchResults::class.java))
        }
        val Messagebtn = findViewById<Button>(R.id.Messagebtn)
        // Set OnClickListener to the arrow_back9 icon
        Messagebtn.setOnClickListener {
            setContentView(R.layout.chat_screen) // Use screen16.xml layout directly
            // Perform fragment transaction to replace frame_container with Screen16 fragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_container, chatscreen())
            fragmentTransaction.commit()
        }
        val Callbtn = findViewById<Button>(R.id.Callbtn)
        Callbtn.setOnClickListener{
            // val intent = Intent(context, Screen20Activity::class.java)
            // startActivity(intent)
            // Check if the device supports telephony features
            if (this.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                // Device supports telephony, proceed with making the call
                val phoneNumber = "03335537550334" // Replace with actual phone number
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")

                // Check for CALL_PHONE permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start the call
                    sendNotification("Initiating Call", "")
                    startActivity(callIntent)
                } else {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_CODE_CALL_PHONE)
                }
            } else {
                // Device does not support telephony, inform the user
                Toast.makeText(this, "This device does not support phone calls.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun sendNotification(title: String, message: String) {
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,
            ViewSearchResult.NOTIFICATION_CHANNEL_ID
        )

            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ViewSearchResult.NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(ViewSearchResult.NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commitAllowingStateLoss()
    }
}