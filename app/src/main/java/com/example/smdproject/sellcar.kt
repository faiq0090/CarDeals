package com.example.smdproject
import android.content.ContentValues
import com.example.smdproject.db.CarDatabaseHelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smdproject.R
import com.example.smdproject.com.example.smdproject.apiconfig.ApiConf
import org.json.JSONObject
import java.io.ByteArrayOutputStream
class sellcar : Fragment() {

    private val TAG = "SELLCAR"
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageBase64: String? = null
    private lateinit var imageButton: ImageButton

    // UI elements
    private lateinit var carType: Spinner
    private lateinit var carModel: Spinner
    private lateinit var city: Spinner
    private lateinit var model: Spinner
    private lateinit var registered: Spinner
    private lateinit var color: Spinner
    private lateinit var fuelType: Spinner
    private lateinit var bodyType: Spinner
    private lateinit var transmissionType: Spinner

    private lateinit var engineCapacity: EditText
    private lateinit var km: EditText
    private lateinit var price: EditText
    private lateinit var description: EditText
    private lateinit var address: EditText
    private lateinit var postBtn: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sellcar, container, false)

        // Bind UI elements
        imageButton = view.findViewById(R.id.button2)
        carType = view.findViewById(R.id.carType)
        carModel = view.findViewById(R.id.carModel)
        city = view.findViewById(R.id.city)
        model = view.findViewById(R.id.model)
        registered = view.findViewById(R.id.registered)
        color = view.findViewById(R.id.color)
        fuelType = view.findViewById(R.id.fuelType)
        bodyType = view.findViewById(R.id.bodyType)
        transmissionType = view.findViewById(R.id.transmissionType)

        engineCapacity = view.findViewById(R.id.EngineCapacity)
        km = view.findViewById(R.id.km)
        price = view.findViewById(R.id.price)
        description = view.findViewById(R.id.desc)
        address = view.findViewById(R.id.Address)
        postBtn = view.findViewById(R.id.button1)

        // Set spinner data
        val fuel = arrayOf("Petrol", "Diesel", "EV", "Hybrid")
        val carModels = arrayOf("Toyota", "Honda", "Suzuki", "KIA", "Hyundai", "Other")
        val cities =
            arrayOf("Islamabad", "Lahore", "Karachi", "Peshawar", "Multan", "Quetta", "Faisalabad")
        val carTypes = arrayOf("SUV", "Sedan", "Hatchback", "MiniVan", "Other")
        val years = (1990..2024).map { it.toString() }.toTypedArray()
        val bodyTypes = arrayOf("Convertible", "Coupe", "Wagon", "Jeep", "Pickup", "Van")
        val colors = arrayOf("White", "Black", "Grey", "Silver", "Red", "Blue", "Other")
        val registeredList = arrayOf("Yes", "No")
        val transmissionList = arrayOf("Manual", "Automatic")

        carType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, carTypes)
        carModel.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, carModels)
        city.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
        model.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        bodyType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, bodyTypes)
        color.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, colors)
        fuelType.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fuel)
        registered.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            registeredList
        )
        transmissionType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            transmissionList
        )

        // Image Picker
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Upload Button
        postBtn.setOnClickListener {
            if (selectedImageBase64 == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT)
                    .show()
            } else {
                uploadCarData()
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                imageButton.setImageURI(it)
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                }

                selectedImageBase64 = encodeImageToBase64(bitmap)
                Log.d(TAG, "Image converted to Base64")
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun uploadCarData() {
        val userId = 4 // Replace with your logic to get user ID
        val url = ApiConf.BASEURL + "Ads/uploadad.php"
        val queue = Volley.newRequestQueue(requireContext())

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Car uploaded successfully!", Toast.LENGTH_LONG)
                    .show()
                Log.d(TAG, "Upload success: $response")

                // Save to SQLite
                val dbHelper = CarDatabaseHelper(requireContext())
                val values = ContentValues().apply {
                    put("user_id", userId)
                    put("car_type", carType.selectedItem.toString())
                    put("car_model", carModel.selectedItem.toString())
                    put("city", city.selectedItem.toString())
                    put("model", model.selectedItem.toString())
                    put("registered", registered.selectedItem.toString())
                    put("color", color.selectedItem.toString())
                    put("fuel_type", fuelType.selectedItem.toString())
                    put("body_type", bodyType.selectedItem.toString())
                    put("transmission_type", transmissionType.selectedItem.toString())
                    put("engine_capacity", engineCapacity.text.toString())
                    put("km_driven", km.text.toString())
                    put("price", price.text.toString())
                    put("description", description.text.toString())
                    put("address", address.text.toString())
                    put("image_base64", selectedImageBase64 ?: "")
                }

                val isSaved = dbHelper.insertCarAd(values)
                if (isSaved) {
                    Log.d(TAG, "Data also saved locally in SQLite")
                } else {
                    Log.e(TAG, "Failed to save data locally")
                }
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Upload failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e(TAG, "Volley Error: ${error.message}", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["user_id"] = userId.toString()
                params["car_type"] = carType.selectedItem.toString()
                params["car_model"] = carModel.selectedItem.toString()
                params["city"] = city.selectedItem.toString()
                params["model"] = model.selectedItem.toString()
                params["registered"] = registered.selectedItem.toString()
                params["color"] = color.selectedItem.toString()
                params["fuel_type"] = fuelType.selectedItem.toString()
                params["body_type"] = bodyType.selectedItem.toString()
                params["transmission_type"] = transmissionType.selectedItem.toString()
                params["engine_capacity"] = engineCapacity.text.toString()
                params["km_driven"] = km.text.toString()
                params["price"] = price.text.toString()
                params["description"] = description.text.toString()
                params["address"] = address.text.toString()
                params["image"] = selectedImageBase64 ?: ""
                return params
            }
        }

        queue.add(request)
    }
}

