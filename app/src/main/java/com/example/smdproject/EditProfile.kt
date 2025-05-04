package com.example.smdproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditProfile: AppCompatActivity() {

    private lateinit var userRef: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "editprofile_channel"
        private const val NOTIFICATION_ID = 124
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        emailEditText = findViewById(R.id.email)
        nameEditText = findViewById(R.id.name)
        phoneEditText = findViewById(R.id.phone)

        currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            userRef = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(user.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val email = dataSnapshot.child("email").getValue(String::class.java)
                        val name = dataSnapshot.child("name").getValue(String::class.java)
                        val phone = dataSnapshot.child("phone").getValue(String::class.java)

                        emailEditText.setText(email)
                        nameEditText.setText(name)
                        phoneEditText.setText(phone)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }

        val UpdateProfile1: Button = findViewById(R.id.button1)

        UpdateProfile1.setOnClickListener {

            val database = FirebaseDatabase.getInstance()
            val databaseRef = database.getReference("Users")

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            val updatedName = findViewById<EditText>(R.id.name).text.toString()
            val updatedPhone = findViewById<EditText>(R.id.phone).text.toString()
            val updatedEmail = findViewById<EditText>(R.id.email).text.toString()

            val updatedUser = hashMapOf(
                "email" to updatedEmail,
                "name" to updatedName,
                "phone" to updatedPhone
            )


            userId?.let {
                databaseRef.child(it).updateChildren(updatedUser as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                        sendNotification("Profile Edited", "Profile Edited")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val arrowBack16 =findViewById<ImageView>(R.id.arrow_back22)

        // Set OnClickListener to the arrow_back9 icon
        arrowBack16.setOnClickListener {
            // Replace the current fragment with the Screen8 fragment
            openFragment()
        }

    }
    private fun sendNotification(title: String, message: String) {
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this,
            EditProfile.NOTIFICATION_CHANNEL_ID
        )

            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                EditProfile.NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(EditProfile.NOTIFICATION_ID, notificationBuilder.build())
    }
    private fun openFragment() {
        // Create an instance of your fragment
        val fragment = profile()

        // Get the FragmentManager
        val fragmentManager = supportFragmentManager

        // Start a new FragmentTransaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the existing fragment (if any) with your fragment
        transaction.replace(R.id.frame_container, fragment)

        // Commit the transaction
        transaction.commit()
    }
}