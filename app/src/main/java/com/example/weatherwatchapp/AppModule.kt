package com.example.weatherwatchapp

import com.example.weatherwatchapp.ui.main.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val BASE_URL: String = "Any"
val appModule = module {

    viewModel { MainActivityViewModel(get()) }

}


