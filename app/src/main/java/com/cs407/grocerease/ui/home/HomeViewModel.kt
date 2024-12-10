package com.cs407.grocerease.ui.home

import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cs407.grocerease.R


class HomeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
    }





    val text: LiveData<String> = _text
}