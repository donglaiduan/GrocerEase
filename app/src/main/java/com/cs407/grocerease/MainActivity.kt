package com.cs407.grocerease

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.getInstance()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val db = FirebaseFirestore.getInstance()
        val blog2 = Blog(username = "null", description = "this is jacob's description", url = "jacoburl")

        val blog3 = Blog(
            username = "brother",
            description = "adfadfas",
            url = "fake.url",
            timestamp = System.currentTimeMillis()
        )




        val email: String = "hello@gmail.com"
        val password: String = "helloWorld"

        signIn(email, password)
//        db.collection("blogs").add(blog3)
//        signOut()
//        db.collection("blogs").add(blog2)


        val blogList = mutableListOf<Blog>()

        db.collection("blogs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val blog = document.toObject(Blog::class.java)
                    blogList.add(blog)
                }
                Log.d("Firestore", "Fetched ${blogList.size} blogs")
                // Pass blogList to your RecyclerView adapter or display logic
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching blogs", e)
            }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

fun signOut() {
    auth.signOut()
    println("User signed out successfully")
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



