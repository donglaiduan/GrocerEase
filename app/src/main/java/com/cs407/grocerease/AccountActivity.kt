package com.cs407.grocerease;
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class AccountActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var weightValueTextView: TextView
    private lateinit var daysValueTextView: TextView
    private lateinit var genderSpinner: Spinner
    private lateinit var saveButton: Button

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

        // Set up the gender spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }

        // Mock: Load initial data (replace with actual Firebase or SharedPreferences fetch)
        loadUserData()

        // Button click listeners
        findViewById<Button>(R.id.btnDecreaseWeight).setOnClickListener { adjustWeight(-1) }
        findViewById<Button>(R.id.btnIncreaseWeight).setOnClickListener { adjustWeight(1) }
        findViewById<Button>(R.id.btnDecreaseDays).setOnClickListener { adjustDays(-1) }
        findViewById<Button>(R.id.btnIncreaseDays).setOnClickListener { adjustDays(1) }
        saveButton.setOnClickListener { saveUserData() }
    }

    private fun loadUserData() {
        // Example initial data (replace with actual data loading logic)
        emailEditText.setText("example@gmail.com")
        usernameEditText.setText("PositivePanda44")
        weightValueTextView.text = "160 Pounds"
        daysValueTextView.text = "7 Days"
        genderSpinner.setSelection(0) // Set default gender selection (e.g., "Male")
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
        val weight = weightValueTextView.text.toString()
        val days = daysValueTextView.text.toString()
        val gender = genderSpinner.selectedItem.toString()

        // Save to Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val userMap = mapOf(
            "email" to email,
            "username" to username,
            "weight" to weight,
            "days" to days,
            "gender" to gender
        )

        db.collection("users")
            .document("currentUser") // Replace with dynamic user ID
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
