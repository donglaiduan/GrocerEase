package com.cs407.grocerease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)



        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()



            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                signUp("test@gmail.com", "test@gmail.com")
                signIn(email, password)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // Navigate to the next activity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", email)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            }
        }

        registerTextView.setOnClickListener {
            // Navigate to the registration screen
            //val intent = Intent(this, RegisterActivity::class.java)
            //startActivity(intent)
        }
    }
}

val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

// Sign In
fun signIn(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // User signed in successfully
                val user = auth.currentUser
                println("User signed in: ${user?.email}")
            } else {
                // Sign-in failed
                println("Sign-in error: ${task.exception?.message}")
            }
        }
}
