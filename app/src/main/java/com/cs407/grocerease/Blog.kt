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
    val userId: String? = null,
    val username: String,
    val email: String,
    val weight: Int,
    val height: Int,
    val age: Int,
    val days: Int,
    val gender: String
) {
    constructor() : this("", "", "", 0, 0, 0, 7, "")
}

