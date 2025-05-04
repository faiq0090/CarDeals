package com.example.smdproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class profile : Fragment() {

    private lateinit var userRef: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var switchDarkMode: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val themeMode = sharedPref.getInt("themeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        applyTheme(themeMode)

        val EditIcon = view.findViewById<Button>(R.id.Editbutton1)
        EditIcon.setOnClickListener {
            val intent = Intent(context, EditProfile::class.java)
            startActivity(intent)
        }

        emailEditText = view.findViewById(R.id.email)
        nameEditText = view.findViewById(R.id.name)
        phoneEditText = view.findViewById(R.id.phone)

        switchDarkMode = view.findViewById(R.id.switch1)
        // Example of toggling dark mode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val themeMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            applyTheme(themeMode)
            saveThemePreference(themeMode)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }


    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun applyTheme(themeMode: Int) {
        when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun saveThemePreference(themeMode: Int) {
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("themeMode", themeMode).apply()
    }


}