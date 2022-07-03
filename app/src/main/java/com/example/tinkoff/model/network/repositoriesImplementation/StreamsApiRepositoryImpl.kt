package com.example.tinkoff.model.network.repositoriesImplementation

import com.example.tinkoff.model.network.api.StreamsJson
import com.example.tinkoff.model.network.api.TopicsJson
import com.example.tinkoff.model.network.repositories.StreamsApiRepository
import com.example.tinkoff.model.network.services.StreamsService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class StreamsApiRepositoryImpl @Inject constructor() : StreamsApiRepository {
    @Inject
    lateinit var streamsService: StreamsService
    override fun getTopicsOfTheStream(id: Int): Single<TopicsJson> {
        return streamsService.getTopicsOfTheStream(id)
    }

    override fun getSubscribedStreams(): Single<StreamsJson> {
        return streamsService.getSubscribedStreams()
    }

    override fun getAllStreams(): Single<StreamsJson> {
        return streamsService.getAllStreams()
    }
}
