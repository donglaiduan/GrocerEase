package com.cs407.grocerease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class AccountActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var weightValueTextView: TextView
    private lateinit var daysValueTextView: TextView
    private lateinit var genderSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var logOutButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        // Initialize views
        emailEditText = findViewById(R.id.etEmail)
        usernameEditText = findViewById(R.id.etUsername)
        weightValueTextView = findViewById(R.id.tvWeightValue)
        daysValueTextView = findViewById(R.id.tvDaysValue)
        genderSpinner = findViewById(R.id.spinnerGender)
        saveButton = findViewById(R.id.btnSave)
        cancelButton = findViewById(R.id.btnCancel)
        logOutButton = findViewById(R.id.btnLogout)

        // Set up the gender spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }

        // Load user data from Firestore
        loadUserData()

        // Button click listeners
        findViewById<Button>(R.id.btnDecreaseWeight).setOnClickListener { adjustWeight(-1) }
        findViewById<Button>(R.id.btnIncreaseWeight).setOnClickListener { adjustWeight(1) }
        findViewById<Button>(R.id.btnDecreaseDays).setOnClickListener { adjustDays(-1) }
        findViewById<Button>(R.id.btnIncreaseDays).setOnClickListener { adjustDays(1) }
        saveButton.setOnClickListener { saveUserData() }
        cancelButton.setOnClickListener{
            finish()
        }
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // Navigate to the LoginActivity (or your app's login screen)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun loadUserData() {
        if(userId != null ) {
            Log.d("userId", userId)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject<User>()
                        user?.let {
                            emailEditText.setText(it.email)
                            usernameEditText.setText(it.username)
                            weightValueTextView.text = "${it.weight} Pounds"
                            daysValueTextView.text = "${it.days} Days"

                            // Set gender selection
                            val genderArray = resources.getStringArray(R.array.gender_options)
                            val genderIndex = genderArray.indexOf(it.gender)
                            if (genderIndex != -1) {
                                genderSpinner.setSelection(genderIndex)
                            }
                        }
                    } else {
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    private fun adjustWeight(delta: Int) {
        val currentWeight = weightValueTextView.text.toString().replace(" Pounds", "").toInt()
        val newWeight = (currentWeight + delta).coerceAtLeast(0)
        weightValueTextView.text = "$newWeight Pounds"
    }

    private fun adjustDays(delta: Int) {
        val currentDays = daysValueTextView.text.toString().replace(" Days", "").toInt()
        val newDays = (currentDays + delta).coerceAtLeast(0)
        daysValueTextView.text = "$newDays Days"
    }

    private fun saveUserData() {
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val weight = weightValueTextView.text.toString().replace(" Pounds", "").toInt()
        val days = daysValueTextView.text.toString().replace(" Days", "").toInt()
        val gender = genderSpinner.selectedItem.toString()

        val user = User(
            userId = userId,
            email = email,
            username = username,
            weight = weight,
            days = days,
            gender = gender,
        )
//        val user = User(
//            userId = userId,
//            email = email,
//            username = username,
//            weight = weight,
//            days = days,
//            gender = gender
//        )

        if(userId != null) {
            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, HomeActivity::class.java)
//                    startActivity(intent)
//                    finish()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}
