package com.cs407.grocerease

import com.google.firebase.firestore.FirebaseFirestore

data class Blog(
    val username: String?,
    val title: String,
    val description: String,
    var url: String,
    val timestamp: Long = 0L
) {
    constructor() : this("", "", "", "", 0L)
}

data class User(
    val userId: String? = "", // Unique identifier for the user
    val username: String = "",   // User's full name
    val email: String = "",  // User's email address
    val weight: Int = 0,
    val age: Int? = 0,        // User's age
    val days: Int = 7,
    val gender: String = ""
) {
    constructor() : this("", "", "", 0, 0, 7, "")
}


fun fetchAllBlogs(
    onSuccess: (List<Blog>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance() // Firestore instance
    val blogCollection = db.collection("blogs") // Reference to the "blog" collection

    // Fetch all blogs
    blogCollection.get()
        .addOnSuccessListener { documents ->
            // Map documents to Blog objects
            val blogs = documents.mapNotNull { it.toObject(Blog::class.java) }
            onSuccess(blogs) // Return the list of blogs
        }
        .addOnFailureListener { exception ->
            onFailure(exception) // Handle the failure
        }
}