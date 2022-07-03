package com.example.tinkoff.model.room.repositories

import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.TopicHeader
import io.reactivex.Completable
import io.reactivex.Single

interface StreamsRoomRepository {
    fun getStreamsByType(type: List<StreamType>): Single<List<StreamHeader>>
    fun insertStreams(list: List<StreamHeaderRoom>): Completable
    fun deleteStreamsByType(type: StreamType): Completable
    fun updateStreamsByTypeAndIndex(type: StreamType, listOfIndexes: List<Int>): Completable
    fun getTopicsByStreamId(ids: List<Int>): Single<List<TopicHeader>>
    fun insertTopics(list: List<TopicHeaderRoom>): Completable
}