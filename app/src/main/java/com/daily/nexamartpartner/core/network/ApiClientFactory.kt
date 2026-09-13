package com.daily.nexamartpartner.core.network

import com.daily.nexamartpartner.core.config.AppConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClientFactory {
    fun create(
        authHeaderInterceptor: AuthHeaderInterceptor,
        connectivityMonitor: NetworkConnectivityMonitor
    ): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authHeaderInterceptor)
            .addInterceptor(OfflineAwareInterceptor(connectivityMonitor))
            .addInterceptor(SafeReadRetryInterceptor(maxRetries = 1))

        if (AppConfig.enableNetworkLogging) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(interceptor)
        }

        // All network DTOs are Kotlin data classes. Retrofit's Moshi converter
        // must use KotlinJsonAdapterFactory; otherwise Moshi rejects Kotlin
        // classes at runtime with "Unable to create converter".
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConfig.baseUrl)
            .client(httpClient.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}
