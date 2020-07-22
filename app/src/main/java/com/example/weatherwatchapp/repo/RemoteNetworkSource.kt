package com.example.weatherwatchapp.repo

import com.example.weatherwatchapp.repo.data.WeatherAPIData
import retrofit2.Response

class RemoteNetworkSource(private val service: ServicesAPI) {
    suspend fun getWeather(cityCode:Int): Response<WeatherAPIData> {
        return service.getLatestMovies(cityCode)
    }
}
