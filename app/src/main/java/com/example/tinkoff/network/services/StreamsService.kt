package com.example.tinkoff.network.services

import com.example.tinkoff.data.api.StreamsJson
import com.example.tinkoff.data.api.TopicsJson
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamsService {
    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<StreamsJson>

    @GET("streams")
    fun getAllStreams(): Single<StreamsJson>

    @GET("users/me/{streamId}/topics")
    fun getTopicsOfTheStream(@Path("streamId") streamId: Int): Single<TopicsJson>
}