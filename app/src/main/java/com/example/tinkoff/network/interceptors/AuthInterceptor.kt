package com.example.tinkoff.network.interceptors

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader("Authorization", API_KEY).build()
        return chain.proceed(request)
    }

    companion object {
        private val API_KEY =
            Credentials
                .basic("ustinovgo@gmail.com", "ilBOCMtOymHL1lVody4X1enQeAIY8FXR")
    }
}
