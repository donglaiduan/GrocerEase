package com.cs407.grocerease

import com.google.firebase.firestore.FirebaseFirestore

data class Blog(
    val username: String,
    val description: String,
    val url: String,
    val timestamp: Long = 0L
) {
    constructor() : this("", "", "", 0L)
}

fun fetchAllBlogs(
    onSuccess: (List<Blog>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance() // Firestore instance
    val blogCollection = db.collection("blog") // Reference to the "blog" collection

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