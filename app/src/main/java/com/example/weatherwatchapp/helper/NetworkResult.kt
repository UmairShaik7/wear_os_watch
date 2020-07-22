package com.example.weatherwatchapp.helper


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class NetworkResult<out R> {

    open class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}
