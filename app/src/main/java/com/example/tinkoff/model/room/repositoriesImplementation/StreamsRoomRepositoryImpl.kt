package com.example.tinkoff.model.room.repositoriesImplementation

import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import com.example.tinkoff.model.room.repositories.StreamsRoomRepository
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.TopicHeader
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class StreamsRoomRepositoryImpl @Inject constructor() : StreamsRoomRepository {
    @Inject
    lateinit var streamsDao: StreamsDao
    @Inject
    lateinit var topicsDao: TopicsDao
    override fun getStreamsByType(type: List<StreamType>): Single<List<StreamHeader>> {
        return streamsDao.getStreamsByType(type.map { it.ordinal })
            .map { list -> list.map { it.toStreamHeader() } }
    }

    override fun insertStreams(list: List<StreamHeaderRoom>): Completable {
        return streamsDao.insertStreams(list)
    }

    override fun deleteStreamsByType(type: StreamType): Completable {
        return streamsDao.deleteStreamsByType(type.ordinal)
    }

    override fun updateStreamsByTypeAndIndex(type: StreamType, listOfIndexes: List<Int>): Completable {
        return streamsDao.updatedStreams(type.ordinal, listOfIndexes)
    }

    override fun getTopicsByStreamId(ids: List<Int>): Single<List<TopicHeader>> {
        return topicsDao.getTopicsByStream(ids)
            .map { list -> list.map { it.toTopicHeader() } }
    }

    override fun insertTopics(list: List<TopicHeaderRoom>): Completable {
        return topicsDao.insertTopics(list)
    }
}
