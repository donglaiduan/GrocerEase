package com.cs407.grocerease.ui

data class Blog(
    val username: String,
    val description: String,
    val url: String,
    val timestamp: Long = 0L
) {
    constructor() : this("", "", "", 0L)
}