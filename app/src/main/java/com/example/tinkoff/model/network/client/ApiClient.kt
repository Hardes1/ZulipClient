package com.example.tinkoff.model.network.client

import com.example.tinkoff.model.network.interceptors.AuthInterceptor
import com.example.tinkoff.model.network.services.MessagesService
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.model.network.services.UsersService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://tinkoff-android-spring-2022.zulipchat.com/api/v1/"
private const val WAIT_TIME: Long = 40
private val client = OkHttpClient().newBuilder().apply {
    addInterceptor(AuthInterceptor()).addInterceptor(
        HttpLoggingInterceptor {
            Timber.d(it)
        }
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
    ).connectTimeout(WAIT_TIME, TimeUnit.SECONDS).callTimeout(WAIT_TIME, TimeUnit.SECONDS)
}.build()
private val json = Json { ignoreUnknownKeys = true }

private val retrofit by lazy {
    val contentType = "application/json".toMediaType()
    Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .addConverterFactory(json.asConverterFactory(contentType))
        .baseUrl(BASE_URL)
        .build()
}

object ApiClient {
    val messagesService: MessagesService by lazy { retrofit.create(MessagesService::class.java) }
    val streamsService: StreamsService by lazy { retrofit.create(StreamsService::class.java) }
    val usersService: UsersService by lazy { retrofit.create(UsersService::class.java) }
}
