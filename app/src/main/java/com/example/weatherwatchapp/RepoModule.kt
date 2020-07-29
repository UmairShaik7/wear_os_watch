package com.example.weatherwatchapp

import com.example.weatherwatchapp.repo.RemoteNetworkSource
import com.example.weatherwatchapp.repo.ServicesAPI
import com.example.weatherwatchapp.repo.WeatherRepository
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val BASE_URL: String = "https://api.openweathermap.org/"
val repoModule = module {
    single { getHttpLogging() }
    single { APIKeyInterceptor() }
    single { getOkHTTPBuilder(get(), get()) }
    single { getRetrofit(get(), Gson()) }
    single { RemoteNetworkSource(createWebService<ServicesAPI>(get())) }
    single { WeatherRepository(get()) }
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
        .readTimeout(60L, TimeUnit.SECONDS)
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()
}

class APIKeyInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("appid", "4b0151007b2b506fe4573e9067c96926")
            .addQueryParameter("units","metric")
            .build()
        val requestBuilder: Request.Builder = original.newBuilder()
            .url(url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}

fun getHttpLogging(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
    else HttpLoggingInterceptor.Level.NONE
    return interceptor
}
