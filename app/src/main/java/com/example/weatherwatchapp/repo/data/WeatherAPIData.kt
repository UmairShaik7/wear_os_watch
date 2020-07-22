package com.example.weatherwatchapp.repo.data

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherAPIData(

    @SerializedName("weather")
    @Expose
    var weather: List<Weather>? = null,
    @SerializedName("main")
    @Expose
    var main: Main
) : Parcelable

@Parcelize
data class Main(
    @SerializedName("temp")
    @Expose
    var temp: Float
) : Parcelable

@Parcelize
data class Weather(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("main")
    @Expose
    var main: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("icon")
    @Expose
    var icon: String? = null
) : Parcelable

