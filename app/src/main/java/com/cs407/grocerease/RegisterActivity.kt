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
import com.google.firebase.firestore.FirebaseFirestore

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
        val usernameEdit = findViewById<EditText>(R.id.usernameEdit)

        val genderOptions = arrayOf("Select Gender", "Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter

        registerButton.setOnClickListener {
            val username = usernameEdit.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val weight = weightEditText.text.toString().trim()
            val selectedGender = genderSpinner.selectedItem.toString()
            if (email.isEmpty() || password.isEmpty() || weight.isEmpty() || selectedGender == "Select Gender") {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                signUp(email, password, username, weight.toInt(), selectedGender)
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

private val auth: FirebaseAuth = FirebaseAuth.getInstance()

fun signUp(email: String, password: String, username: String, weight: Int, selectedGender: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val db = FirebaseFirestore.getInstance()
                val user = auth.currentUser

                val userId = user?.uid
                val currUser = User(
                    userId = userId,
                    username = username,
                    email = email,
                    weight = weight.toInt(),
                    days = 7,
                    gender = selectedGender
                )
                if(userId != null) {
                    db.collection("users").document(userId).set(currUser)
                        .addOnSuccessListener {
                            println("User details saved successfully!")
                        }
                        .addOnFailureListener { e ->
                            println("Error saving user details: $e")
                        }
                }

                println("User signed up: ${user?.email}")
            } else {
                println("Sign-up error: ${task.exception?.message}")
            }
        }
}
