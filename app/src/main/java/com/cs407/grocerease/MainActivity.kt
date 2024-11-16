package com.cs407.grocerease

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val db = FirebaseFirestore.getInstance()
        val blog2 = Blog(username = "jacob", description = "this is jacob's description", url = "jacoburl")

        val blog3 = object {
            var username = "jacob";
            var description = "adfadfas";
            var url = "fake.url"
        }

        db.collection("blogger").add(blog3)
        db.collection("blog").add(blog2)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

val db = FirebaseFirestore.getInstance()

fun saveUserData(user: User) {
    db.collection("usersTest")
        .document("QaIHSd0X1tiZ2JYu4NT4")
        .update("user", user)
        .addOnSuccessListener {
            // Handle success
            Log.d("Firestore", "User data added successfully")
        }
        .addOnFailureListener { e ->
            // Handle failure
            Log.e("Firestore", "Error adding user data", e)
        }
}

fun getUserData(userId: String) {
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject(User::class.java)
                Log.d("Firestore", "User data: $user")
            } else {
                Log.d("Firestore", "No such document")
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error getting document", e)
        }
}
