package com.cs407.grocerease.ui.home

import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.grocerease.R
import com.cs407.grocerease.fetchAllBlogs


class HomeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
//        value = "Blog Posts"
    }

//    fetchAllBlogs()




    val text: LiveData<String> = _text
}