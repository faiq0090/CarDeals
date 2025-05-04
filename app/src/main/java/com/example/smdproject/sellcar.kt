package com.example.smdproject

import Car
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class sellcar : Fragment() {

    //To Store Related Car information
    private lateinit var city: String
    private lateinit var model1: String
    private lateinit var registered1: String
    private lateinit var color1: String
    private lateinit var km: String
    private lateinit var price: String
    private lateinit var desc: String
    private lateinit var carCompany: String
    private lateinit var carModel: String
    private lateinit var carFuelType: String

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var profileIconImageView: ImageButton

    private var profilePictureUri: Uri? = null
    private var profilePictureUrl: String? = null

    private var carDatabaseHelper: DatabaseHelper? = null

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "sellcar_channel"
        private const val NOTIFICATION_ID = 124
    }

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sellcar, container, false)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        profileIconImageView = view.findViewById(R.id.button2)

        val AddCar: Button = view.findViewById(R.id.button1)

        val carModels = arrayOf("Civic","Vezel","Brv","Alto","Wagon R" ,"Swift","Civic Turbo", "Grande", "Cultus", "City", "Yaris", "LandCruiser V8", "E-Tron", "c63 AMG","Revo")

        val spinnerModel: Spinner = view.findViewById(R.id.carModel)
        val adapterModel = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, carModels)
        adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModel.adapter = adapterModel

        val carTypes = arrayOf("Honda", "Toyota", "Suzuki", "Hyundai", "Audi", "BMW", "Mercedes")

        val spinnerCompany: Spinner = view.findViewById(R.id.carType)
        val adapterCompany = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, carTypes)
        adapterCompany.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCompany.adapter = adapterCompany

        val spinnerCity: Spinner = view.findViewById(R.id.city)
        val cities = arrayOf("Islamabad", "Lahore", "Karachi")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = adapter

        val spinnermodel: Spinner = view.findViewById(R.id.model)
        var model = arrayOf("2020", "2021", "2022", "2023", "2024")
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, model)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnermodel.adapter = adapter2

        val spinnerRegistered: Spinner = view.findViewById(R.id.registered)
        var registered = arrayOf("Islamabad", "Lahore", "Karachi")
        val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, registered)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRegistered.adapter = adapter3

        val spinnerColor: Spinner = view.findViewById(R.id.color)
        var color = arrayOf("Black", "Silver", "White","Red")
        val adapter4 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, color)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = adapter4

        val spinnerFuelType: Spinner = view.findViewById(R.id.FuelType)
        var FuelType = arrayOf("Petrol", "Diesel", "Electric")
        val adapter5 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, FuelType)
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFuelType.adapter = adapter5

        val bodyTypes = arrayOf("Sedan", "Hatchback", "SUV", "Coupe", "Convertible", "Minivan", "Pickup", "Crossover", "Sports", "Japanese", "Imported")

        val spinnerBodyType: Spinner = view.findViewById(R.id.BodyType)
        val adapter6 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bodyTypes)
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBodyType.adapter = adapter6

        val transmissionTypes = arrayOf("Manual", "Automatic")

        val Transmissionspinner: Spinner = view.findViewById(R.id.TransmissionType)
        val adapter7 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, transmissionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Transmissionspinner.adapter = adapter7

        basicReadWrite()
        childadd()

        carDatabaseHelper = DatabaseHelper(requireContext())

        AddCar.setOnClickListener {

            if (isNetworkAvailable()) {
                addValueFirebase(profilePictureUrl)
                sendNotification("Ad Uploaded", "")
            } else {
                addValueSQLite(profilePictureUrl)
                sendNotification("Ad Uploaded", "")
            }
        }

        profileIconImageView.setOnClickListener {

            openGalleryForImage()
        }

        return view
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                profilePictureUri = uri
                // You can display the selected image here if needed
                profileIconImageView.setImageURI(profilePictureUri)

                profileIconImageView.isEnabled= false
                saveCarImage()
            }
        }

    private fun openGalleryForImage() {
        galleryLauncher.launch("image/*")
    }

    private fun saveCarImage() {
        if (profilePictureUri == null) {
            // Handle case when user didn't select a profile picture
            return
        }

        val carId = UUID.randomUUID().toString()

        val carImageRef = storage.reference.child("Car_Ads/$carId")

        carImageRef.putFile(profilePictureUri!!)
            .addOnSuccessListener { uploadTask ->
                carImageRef.downloadUrl.addOnSuccessListener { imageUri ->

                    // Update
                    profilePictureUrl = imageUri.toString()

                    // Add this to the function saveCarImage
                    saveUserProfile()

                }.addOnFailureListener { exception ->
                    // Handle failure to retrieve download URL
                    Log.e("FirebaseStorage", "Error getting download URL: $exception")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to upload profile picture
                Log.e("FirebaseStorage", "Error uploading picture: $exception")
            }
    }

    private fun saveUserProfile() {
        if (profilePictureUri == null) {
            // Handle case when user didn't select a profile picture
            return
        }

        val userId = mAuth.currentUser?.uid ?: return

        val profilePictureRef = storage.reference.child("car_pictures/${UUID.randomUUID()}")

        profilePictureRef.putFile(profilePictureUri!!)
            .addOnSuccessListener { uploadTask ->
                profilePictureRef.downloadUrl.addOnSuccessListener { downloadUri ->

                    // Update only the download URI in the user data
                    val userUpdates = hashMapOf<String, Any>(
                        "downloadUri" to downloadUri.toString() // Convert to String
                        // Add other fields here if needed
                    )

                    // Update the user data in Realtime Database
                    database.reference.child("cars_pictures").child(userId).updateChildren(userUpdates)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                // Profile saved successfully, navigate to the next activity
                                // Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show()
                                showToast("Image uploaded successfully!")
                                //startActivity(Intent(this, Fragment21::class.java))
                                //finish()
                            } else {
                                // Handle error while saving profile to database
                                Log.e("FirebaseDatabase", "Error updating user data: ${dbTask.exception}")
                            }
                        }
                }.addOnFailureListener { exception ->
                    // Handle failure to retrieve download URL
                    Log.e("FirebaseStorage", "Error getting download URL: $exception")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to upload profile picture
                Log.e("FirebaseStorage", "Error uploading picture: $exception")
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting == true
    }

    fun basicReadWrite() {

        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("message")
        databaseRef.setValue("Hello, World!")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                Log.d("Firebase_db", "Value is:"+value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Firebase_db", "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }

    fun childadd(){

        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("Users")

        databaseRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase_db", "onChildAdded:" + dataSnapshot.key!! )
                //-----

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase_db", "onChildChanged:"+dataSnapshot.key)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d("Firebase_db", "onChildRemoved:" + dataSnapshot.key!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase_db", "onChildMoved:" + dataSnapshot.key!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase_db", "postComments:onCancelled", databaseError.toException())
            }
        })
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set the sound for the notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)

            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.notifications)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri) // Set the notification sound

        // Since Android Oreo, notification channel is required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                sellcar.NOTIFICATION_CHANNEL_ID,
                "Screenshot Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(sellcar.NOTIFICATION_ID, notificationBuilder.build())
    }

    fun addValueFirebase(imageUrl: String?) {
        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("Car_Ads")

        city = view?.findViewById<Spinner>(R.id.city)?.selectedItem.toString()
        model1 = view?.findViewById<Spinner>(R.id.model)?.selectedItem.toString()
        registered1 = view?.findViewById<Spinner>(R.id.registered)?.selectedItem.toString()
        color1 = view?.findViewById<Spinner>(R.id.color)?.selectedItem.toString()
        carCompany = view?.findViewById<Spinner>(R.id.carType)?.selectedItem.toString()
        carModel = view?.findViewById<Spinner>(R.id.carModel)?.selectedItem.toString()
        carFuelType = view?.findViewById<Spinner>(R.id.FuelType)?.selectedItem.toString()
        val transmissionType = view?.findViewById<Spinner>(R.id.TransmissionType)?.selectedItem.toString()
        val bodyType = view?.findViewById<Spinner>(R.id.BodyType)?.selectedItem.toString()
        val address = view?.findViewById<EditText>(R.id.Address)?.text.toString()
        km = view?.findViewById<EditText>(R.id.km)?.text.toString()
        val EngineCapacity = view?.findViewById<EditText>(R.id.EngineCapacity)?.text.toString()
        price = view?.findViewById<EditText>(R.id.price)?.text.toString()
        desc = view?.findViewById<EditText>(R.id.desc)?.text.toString()

        val carId = databaseRef.push().key // Generate unique id for the car

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null && carId != null) {
            val car = hashMapOf(
                "city" to city,
                "model" to model1,
                "registered" to registered1,
                "color" to color1,
                "km" to km,
                "FuelType" to carFuelType,
                "price" to price,
                "desc" to desc,
                "carCompany" to carCompany,
                "carModel" to carModel,
                "transmissionType" to transmissionType,
                "bodyType" to bodyType,
                "address" to address,
                "EngineCapacity" to EngineCapacity,
                "userId" to userId,  // Add userID
                "imageUrl" to imageUrl  // Add image URL
            )

            databaseRef.child(carId).setValue(car)
                .addOnSuccessListener {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Car added successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to add car", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    fun addValueSQLite(imageUrl: String?) {
        city = view?.findViewById<Spinner>(R.id.city)?.selectedItem.toString()
        model1 = view?.findViewById<Spinner>(R.id.model)?.selectedItem.toString()
        registered1 = view?.findViewById<Spinner>(R.id.registered)?.selectedItem.toString()
        color1 = view?.findViewById<Spinner>(R.id.color)?.selectedItem.toString()
        carCompany = view?.findViewById<Spinner>(R.id.carType)?.selectedItem.toString()
        carModel = view?.findViewById<Spinner>(R.id.carModel)?.selectedItem.toString()
        carFuelType = view?.findViewById<Spinner>(R.id.FuelType)?.selectedItem.toString()
        val transmissionType = view?.findViewById<Spinner>(R.id.TransmissionType)?.selectedItem.toString()
        val bodyType = view?.findViewById<Spinner>(R.id.BodyType)?.selectedItem.toString()
        val address = view?.findViewById<EditText>(R.id.Address)?.text.toString()
        km = view?.findViewById<EditText>(R.id.km)?.text.toString()
        val EngineCapacity = view?.findViewById<EditText>(R.id.EngineCapacity)?.text.toString()
        price = view?.findViewById<EditText>(R.id.price)?.text.toString()
        desc = view?.findViewById<EditText>(R.id.desc)?.text.toString()

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val car = Car(
                city = city,
                model = model1,
                registered = registered1,
                color = color1,
                km = km,
                fuelType = carFuelType,
                price = price,
                desc = desc,
                carCompany = carCompany,
                carModel = carModel,
                transmissionType = transmissionType,
                bodyType = bodyType,
                address = address,
                engineCapacity = EngineCapacity,
                userId = userId,
                imageUrl = imageUrl
            )
            val id = carDatabaseHelper?.addCar(car)
            if (id != null) {
                Toast.makeText(requireContext(), "Car added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add car", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        carDatabaseHelper?.close()
    }
}
