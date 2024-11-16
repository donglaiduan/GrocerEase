package com.cs407.grocerease

data class User(
    val userId: String = "",
    val name: String = "",
    val blogData: String = "",
    val age: Int = 0
)

data class Blog(
    val username: String,
    val description: String,
    val url: String,
    val timestamp: Long = 0L
) {
    constructor() : this("", "", "", 0L)
}