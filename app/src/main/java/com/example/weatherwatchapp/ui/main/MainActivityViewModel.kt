package com.example.weatherwatchapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherwatchapp.helper.NetworkResult
import com.example.weatherwatchapp.repo.WeatherRepository
import com.example.weatherwatchapp.repo.data.WeatherAPIData
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: WeatherRepository) : ViewModel() {
    private val _weatherValue = MutableLiveData<WeatherAPIData>()

    val weatherValue: LiveData<WeatherAPIData> = _weatherValue

    val selectedCity = MutableLiveData<String>()

    fun fetchWeather(cityCode: Int) {
        viewModelScope.launch {
            val result = repo.getWeather(cityCode)
            if (result is NetworkResult.Success) {
                _weatherValue.postValue(result.data as WeatherAPIData?)
            }
        }
    }
}

enum class CityCode(val cityCode: Int, val latitude: Float, val longitude: Float){
    CHENNAI(1264527, 13.0827f, 80.2707f),
    BANGALORE(1277333, 12.9716f, 77.5946f)
}