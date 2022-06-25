package com.example.tinkoff.model.room.repositories

import com.example.tinkoff.model.room.dao.MessagesDao
import com.example.tinkoff.presentation.applications.di.FragmentScope
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.Reaction
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class MessagesRoomRepository @Inject constructor() {
    @Inject
    lateinit var messagesDao: MessagesDao

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
