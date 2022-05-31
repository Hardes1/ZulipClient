package com.example.tinkoff.model.network.repositories

import com.example.tinkoff.model.network.api.MessagesListJson
import com.example.tinkoff.model.network.api.SendCallBackJson
import com.example.tinkoff.model.network.api.StatusJson
import com.example.tinkoff.model.network.api.StreamsJson
import com.example.tinkoff.model.network.api.TopicsJson
import com.example.tinkoff.model.network.api.UpdateReactionCallBackJson
import com.example.tinkoff.model.network.api.UsersJson
import com.example.tinkoff.model.network.client.ApiClient
import com.example.tinkoff.model.states.StreamType
import com.example.tinkoff.presentation.classes.User
import io.reactivex.Single

class ApiRepository {
    fun getMessages(
        anchor: String,
        numBefore: Int,
        numAfter: Int,
        narrow: String
    ): Single<MessagesListJson> {
        return ApiClient.messagesService.getMessagesOfTheStream(anchor, numBefore, numAfter, narrow)
    }

    fun getSubscribedStreams() : Single<StreamsJson> {
        return ApiClient.streamsService.getSubscribedStreams()
    }

    fun getAllStreams() : Single<StreamsJson> {
        return ApiClient.streamsService.getAllStreams()
    }

    fun getAllUsers(): Single<UsersJson> {
        return ApiClient.usersService.getAllUsers()
    }

    fun getOwnUser(): Single<User> {
        return ApiClient.usersService.getOwnUser()
    }

    fun sendMessage(
        streamHeader: String,
        topicHeader: String,
        content: String
    ): Single<SendCallBackJson> {
        return ApiClient.messagesService.sendMessage(
            streamHeader = streamHeader, topicHeader = topicHeader,
            content = content
        )
    }

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return ApiClient.messagesService.addReaction(messageId, emojiName)
    }

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return ApiClient.messagesService.removeReaction(messageId, emojiName)
    }

    fun getTopicsOfTheStream(id: Int): Single<TopicsJson> {
        return ApiClient.streamsService.getTopicsOfTheStream(id)
    }

    fun getOnlineUserStatus(userId: Int): Single<StatusJson> {
        return ApiClient.usersService.getUserOnlineStatus(userId)
    }

    companion object {
        const val MY_ID = 493568
        const val ERROR = "Error happened"
    }
}


fun <T> List<Single<T>>.zipSingles(): Single<List<T>> {
    if (this.isEmpty()) return Single.just(emptyList())
    return Single.zip(this) {
        @Suppress("UNCHECKED_CAST")
        return@zip (it as Array<T>).toList()
    }
}

