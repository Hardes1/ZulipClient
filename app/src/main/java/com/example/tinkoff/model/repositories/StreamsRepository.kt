package com.example.tinkoff.model.repositories

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.model.storages.StreamsStorage
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.StreamsInterface
import com.example.tinkoff.presentation.classes.TopicHeader
import io.reactivex.Completable
import io.reactivex.Single

interface StreamsRepository {
    fun getStreamsFromSource(
        streamType: StreamType,
        dataSource: DataSource,
        needToRefresh: Boolean
    ): Single<List<StreamsInterface>>

    fun selectStreamsById(id: Int, isSelected: Boolean): Single<List<StreamsInterface>>

    fun getFilteredStreams(filter: String): Single<List<StreamsInterface>>
}
