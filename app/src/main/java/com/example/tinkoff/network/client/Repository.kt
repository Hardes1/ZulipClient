package com.example.tinkoff.network.client

import com.example.tinkoff.data.api.MessagesListJson
import com.example.tinkoff.data.api.SendCallBackJson
import com.example.tinkoff.data.api.StatusJson
import com.example.tinkoff.data.api.StreamsJson
import com.example.tinkoff.data.api.TopicsJson
import com.example.tinkoff.data.api.UpdateReactionCallBackJson
import com.example.tinkoff.data.classes.User
import com.example.tinkoff.data.api.UsersJson
import com.example.tinkoff.data.states.StreamType
import io.reactivex.Single

object Repository {
    const val MY_ID = 493568
    const val ERROR = "Error happened"

    fun getMessages(
        anchor: String,
        numBefore: Int,
        numAfter: Int,
        narrow: String
    ): Single<MessagesListJson> {
        return Client.messagesService.getMessagesOfTheStream(anchor, numBefore, numAfter, narrow)
    }

    fun getStreams(type: StreamType): Single<StreamsJson> {
        return when (type) {
            StreamType.SUBSCRIBED -> Client.streamsService.getSubscribedStreams()
            StreamType.ALL_STREAMS -> Client.streamsService.getAllStreams()
        }
    }

    fun getAllUsers(): Single<UsersJson> {
        return Client.usersService.getAllUsers()
    }

    fun getOwnUser(): Single<User> {
        return Client.usersService.getOwnUser()
    }

    fun sendMessage(
        streamHeader: String,
        topicHeader: String,
        content: String
    ): Single<SendCallBackJson> {
        return Client.messagesService.sendMessage(
            streamHeader = streamHeader, topicHeader = topicHeader,
            content = content
        )
    }

    fun addReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return Client.messagesService.addReaction(messageId, emojiName)
    }

    fun removeReaction(
        messageId: Int,
        emojiName: String
    ): Single<UpdateReactionCallBackJson> {
        return Client.messagesService.removeReaction(messageId, emojiName)
    }

    fun getTopicsOfTheStream(id : Int) : Single<TopicsJson>{
        return Client.streamsService.getTopicsOfTheStream(id)
    }

    fun getOnlineUserStatus(userId : Int) : Single<StatusJson> {
        return Client.usersService.getUserOnlineStatus(userId)
    }
}
