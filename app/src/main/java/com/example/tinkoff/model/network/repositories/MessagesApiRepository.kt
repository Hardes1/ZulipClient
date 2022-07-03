package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.MessagesListJson
import com.example.tinkoff.model.network.api.SendCallBackJson
import com.example.tinkoff.model.network.api.UpdateReactionCallBackJson
import io.reactivex.Single

interface MessagesApiRepository {
    fun getMessages(
        anchor: String,
        numBefore: Int,
        numAfter: Int,
        narrow: String
    ): Single<MessagesListJson>

    fun sendMessage(
        streamHeader: String,
        topicHeader: String,
        content: String
    ): Single<SendCallBackJson>

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson>

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson>
}