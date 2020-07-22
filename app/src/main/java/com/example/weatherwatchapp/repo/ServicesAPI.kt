package com.example.weatherwatchapp.repo

import retrofit2.Response
import retrofit2.http.GET

interface ServicesAPI {

    @GET("movie/now_playing")
    suspend fun getLatestMovies(): Response<Any>
}
