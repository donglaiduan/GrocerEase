package com.cs407.grocerease

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddBlogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
        val addBlogButton = findViewById<Button>(R.id.addBlogButton)

        addBlogButton.setOnClickListener{
            Toast.makeText(this, "Add Blog Button Clicked!", Toast.LENGTH_SHORT).show()
            Log.d("Blog Button", "Add Blog Button Clicked!")
        }
    }
}