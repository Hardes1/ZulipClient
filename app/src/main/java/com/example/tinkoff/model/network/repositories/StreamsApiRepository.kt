package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StreamsJson
import com.example.tinkoff.model.network.api.TopicsJson
import io.reactivex.Single

interface StreamsApiRepository {
    fun getTopicsOfTheStream(id: Int): Single<TopicsJson>
    fun getSubscribedStreams(): Single<StreamsJson>
    fun getAllStreams(): Single<StreamsJson>
}