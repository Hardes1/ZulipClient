package com.example.tinkoff.model.room.repositories

import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.model.room.dao.StreamsDao
import com.example.tinkoff.model.room.dao.TopicsDao
import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.Reaction
import com.example.tinkoff.presentation.classes.StreamHeader
import com.example.tinkoff.presentation.classes.TopicHeader
import io.reactivex.Completable
import io.reactivex.Single

class RoomRepository(
    private val streamsDao: StreamsDao,
    private val topicsDao: TopicsDao,
    private val messagesDao: MessagesDao
) {
    fun getStreamsByType(type: List<StreamType>): Single<List<StreamHeader>> {
        return streamsDao.getStreamsByType(type.map { it.ordinal })
            .map { list -> list.map { it.toStreamHeader() } }
    }

    fun insertStreams(list: List<StreamHeaderRoom>): Completable {
        return streamsDao.insertStreams(list)
    }

    fun deleteStreamsByType(type: StreamType): Completable {
        return streamsDao.deleteStreamsByType(type.ordinal)
    }

    fun updateStreamsByTypeAndIndex(type: StreamType, listOfIndexes: List<Int>): Completable {
        return streamsDao.updatedStreams(type.ordinal, listOfIndexes)
    }

    fun getTopicsByStreamId(ids: List<Int>): Single<List<TopicHeader>> {
        return topicsDao.getTopicsByStream(ids).map { list -> list.map { it.toTopicHeader() } }
    }

    fun insertTopics(list: List<TopicHeaderRoom>): Completable {
        return topicsDao.insertTopics(list)
    }

    fun getMessagesByStreamAndTopic(
        streamName: String,
        topicName: String
    ): Single<List<MessageContent>> {
        return messagesDao.getMessagesByStreamAndTopic(streamName, topicName)
            .map { list -> list.map { it.toMessageContent() } }
    }

    fun deleteMessagesByStreamAndTopic(streamName: String, topicName: String): Completable {
        return messagesDao.deleteMessagesByStreamAndTopic(streamName, topicName)
    }

    fun insertMessages(
        list: List<MessageContent>,
        streamName: String,
        topicName: String
    ): Completable {
        return messagesDao.insertMessages(
            list.map {
                it.toMessageContentRoom(
                    streamName,
                    topicName
                )
            }
        )
    }

    fun updateMessageReactions(id: Int, reactions: List<Reaction>): Completable {
        return messagesDao.updateMessageReaction(id, reactions)
    }
}
