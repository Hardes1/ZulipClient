package com.example.tinkoff.model.room.repositories

import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.Reaction
import io.reactivex.Completable
import io.reactivex.Single

interface MessagesRoomRepository {
    fun getMessagesByStreamAndTopic(
        streamName: String,
        topicName: String
    ): Single<List<MessageContent>>

    fun deleteMessagesByStreamAndTopic(streamName: String, topicName: String): Completable
    fun insertMessages(
        list: List<MessageContent>,
        streamName: String,
        topicName: String
    ): Completable

    fun updateMessageReactions(id: Int, reactions: List<Reaction>): Completable
}