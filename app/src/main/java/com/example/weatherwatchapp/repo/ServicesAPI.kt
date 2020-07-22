package com.example.weatherwatchapp.repo

import com.example.weatherwatchapp.repo.data.WeatherAPIData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicesAPI {

    @GET("data/2.5/weather")
    suspend fun getLatestMovies(@Query("id") cityCode: Int): Response<WeatherAPIData>
}
