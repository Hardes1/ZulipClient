package com.example.tinkoff.model.repositories

import com.example.tinkoff.model.states.DataSource
import com.example.tinkoff.model.storages.UpdateElement
import com.example.tinkoff.presentation.classes.MessageContentInterface
import io.reactivex.Completable
import io.reactivex.Single

interface MessagesRepository : UpdateElement {
    fun setLastClickedMessageId(id: Int): Completable

    fun getMessagesListWithReaction(
        messagesId: Int,
        reactionName: String,
        isEnabled: Boolean
    ): Single<List<MessageContentInterface>>

    fun filterMessages(filter: String): Single<List<MessageContentInterface>>

    fun trySendMessage(streamHeader: String, topicHeader: String, message: String):
        Single<List<MessageContentInterface>>

    fun updateReaction(reactionIndexValue: Int): Single<List<MessageContentInterface>>

    fun getNewMessages(
        streamHeader: String,
        topicHeader: String
    ): Single<List<MessageContentInterface>>

    fun getMessagesFromSource(
        streamHeader: String,
        topicHeader: String,
        dataSource: DataSource
    ): Single<List<MessageContentInterface>>
}
