package com.example.nutriapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

open class ImageViewModel : ViewModel() {

    val imagePath = mutableStateOf("")

    fun updateImagePath(path: String){
        imagePath.value = path
    }
}