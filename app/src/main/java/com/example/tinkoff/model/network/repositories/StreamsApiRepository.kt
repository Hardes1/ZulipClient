package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.StreamsJson
import com.example.tinkoff.model.network.api.TopicsJson
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class StreamsApiRepository @Inject constructor() {
    @Inject
    lateinit var streamsService: StreamsService
    fun getTopicsOfTheStream(id: Int): Single<TopicsJson> {
        return streamsService.getTopicsOfTheStream(id)
    }

    fun getSubscribedStreams(): Single<StreamsJson> {
        return streamsService.getSubscribedStreams()
    }

    fun getAllStreams(): Single<StreamsJson> {
        return streamsService.getAllStreams()
    }
}
