package com.example.tinkoff.presentation.applications.di

import com.example.tinkoff.model.network.interceptors.AuthInterceptor
import com.example.tinkoff.model.network.services.MessagesService
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.model.network.services.UsersService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String {
        return URL
    }

    @Provides
    @Timeout
    fun provideWaitTime(): Long {
        return TIME
    }

    @Provides
    @Singleton
    fun provideClient(
        @Timeout
        waitTime: Long,
        @Interceptor
        interceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder().apply {
            addInterceptor(interceptor).addInterceptor(
                HttpLoggingInterceptor {
                    Timber.d(it)
                }
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
            ).connectTimeout(waitTime, TimeUnit.SECONDS)
                .callTimeout(waitTime, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    fun provideMediaType(): MediaType {
        return "application/json".toMediaType()
    }

    @Provides
    @Singleton
    fun provideRetrofitService(
        client: OkHttpClient,
        converterFactory: Converter.Factory,
        adapterFactory: RxJava2CallAdapterFactory,
        @BaseUrl
        url: String
    ): Retrofit {
        return Retrofit
            .Builder()
            .addCallAdapterFactory(adapterFactory)
            .client(client)
            .addConverterFactory(converterFactory)
            .baseUrl(url)
            .build()
    }

    @Provides
    @Singleton
    fun provideJsonFactory(json: Json, mediaType: MediaType): Converter.Factory {
        return json.asConverterFactory(mediaType)
    }

    @Provides
    @Singleton
    fun provideAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @Interceptor
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    @Provides
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @Singleton
    fun provideMessagesService(retrofit: Retrofit): MessagesService {
        return retrofit.create(MessagesService::class.java)
    }

    @Provides
    @Singleton
    fun provideStreamsService(retrofit: Retrofit): StreamsService {
        return retrofit.create(StreamsService::class.java)
    }

    @Provides
    @Singleton
    fun provideUsersService(retrofit: Retrofit): UsersService {
        return retrofit.create(UsersService::class.java)
    }

    companion object {
        private const val URL = "https://tinkoff-android-spring-2022.zulipchat.com/api/v1/"
        private const val TIME: Long = 40
    }
}
