package com.example.weatherwatchapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.weatherwatchapp.repo.MainRepository

class MainActivityViewModel(private val repo: MainRepository) : ViewModel() {
    fun getItem(): String {
        return repo.getItem()
    }
}