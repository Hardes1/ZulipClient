package com.example.tinkoff.model.network.repositoriesImplementation

import com.example.tinkoff.model.network.api.MessagesListJson
import com.example.tinkoff.model.network.api.SendCallBackJson
import com.example.tinkoff.model.network.api.UpdateReactionCallBackJson
import com.example.tinkoff.model.network.repositories.MessagesApiRepository
import com.example.tinkoff.model.network.services.MessagesService
import com.example.tinkoff.presentation.applications.di.FragmentScope
import io.reactivex.Single
import javax.inject.Inject

@FragmentScope
class MessagesApiRepositoryImpl @Inject constructor() : MessagesApiRepository {
    @Inject
    lateinit var messagesService: MessagesService
    override fun getMessages(
        anchor: String,
        numBefore: Int,
        numAfter: Int,
        narrow: String
    ): Single<MessagesListJson> {
        return messagesService.getMessagesOfTheStream(anchor, numBefore, numAfter, narrow)
    }

    override fun sendMessage(
        streamHeader: String,
        topicHeader: String,
        content: String
    ): Single<SendCallBackJson> {
        return messagesService.sendMessage(
            streamHeader = streamHeader, topicHeader = topicHeader,
            content = content
        )
    }

    override fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return messagesService.addReaction(messageId, emojiName)
    }

    override fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return messagesService.removeReaction(messageId, emojiName)
    }
}
