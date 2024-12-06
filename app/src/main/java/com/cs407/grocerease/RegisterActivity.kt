package com.cs407.grocerease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailEditText = findViewById<EditText>(R.id.emailEdit)
        val passwordEditText = findViewById<EditText>(R.id.passwordEdit)
        val weightEditText = findViewById<EditText>(R.id.weightEdit)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)

        // Set up the Spinner for gender selection
        val genderOptions = arrayOf("Select Gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val weight = weightEditText.text.toString().trim()
            val selectedGender = genderSpinner.selectedItem.toString()

            if (email.isEmpty() || password.isEmpty() || weight.isEmpty() || selectedGender == "Select Gender") {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                signUp(email, password)
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                // Navigate to the next activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginTextView.setOnClickListener {
            // Navigate to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

private val auth: FirebaseAuth = FirebaseAuth.getInstance()

// Sign Up
fun signUp(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // User registered successfully
                val user = auth.currentUser
                println("User signed up: ${user?.email}")
            } else {
                // Sign-up failed
                println("Sign-up error: ${task.exception?.message}")
            }
        }
}
