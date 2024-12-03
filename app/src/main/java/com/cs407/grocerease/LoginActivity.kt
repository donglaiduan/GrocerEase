package com.cs407.grocerease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val user = signIn(email, password)
                        if (user != null) {
                            // User signed in successfully
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                            // Navigate to the next activity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)

                            // Finish the current activity
                            finish()
                        }
                    } catch (e: Exception) {
                        // Handle sign-in failure
                        Log.e("failed sign in", "Sign-in error: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        registerTextView.setOnClickListener {
            // Navigate to the registration screen
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}

suspend fun signIn(email: String, password: String): FirebaseUser? {
    return try {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .await()
            .user

    } catch (e: Exception) {
        throw Exception("Sign-in failed: ${e.message}", e)
    }
}
