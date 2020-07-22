package com.example.weatherwatchapp.repo

import com.example.weatherwatchapp.helper.NetworkResult
import com.example.weatherwatchapp.helper.NetworkResult.Success
import com.example.weatherwatchapp.repo.data.WeatherAPIData

class MainRepository(private var remoteSource: RemoteNetworkSource) {
    suspend fun getWeather(cityCode:Int): Success<WeatherAPIData?> {
        val result = remoteSource.getWeather(cityCode)
        return NetworkResult.Success(result.body())
    }
}