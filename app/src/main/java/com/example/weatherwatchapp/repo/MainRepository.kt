package com.example.weatherwatchapp.repo

class MainRepository (private var remoteSource: RemoteNetworkSource) {
    fun getItem():String {
        return remoteSource.getItem()
    }
}