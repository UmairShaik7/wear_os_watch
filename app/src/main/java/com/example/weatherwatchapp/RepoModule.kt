package com.example.weatherwatchapp

import com.example.weatherwatchapp.repo.MainRepository
import com.example.weatherwatchapp.repo.RemoteNetworkSource
import com.example.weatherwatchapp.repo.ServicesAPI
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val BASE_URL: String = "https://www.google.com"
val repoModule = module {
    single { getHttpLogging() }
    single { APIKeyInterceptor() }
    single { getOkHTTPBuilder(get(), get()) }
    single { getRetrofit(get(), Gson()) }
    single { RemoteNetworkSource(createWebService<ServicesAPI>(get())) }
    single { MainRepository(get()) }
}

inline fun <reified T> createWebService(retrofit: Retrofit): T = retrofit.create(T::class.java)

fun getRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
    return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
}

fun getOkHTTPBuilder(
    apiKeyInterceptor: APIKeyInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient.Builder().connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor)
        .addInterceptor(apiKeyInterceptor)
        .build()

}

class APIKeyInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val response = chain.proceed(requestBuilder.build())

        return response
    }
}

fun getHttpLogging(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
    else HttpLoggingInterceptor.Level.NONE
    return interceptor
}
