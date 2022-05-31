package com.example.tinkoff.presentation.classes

import com.example.tinkoff.model.room.entities.MessageContentRoom
import com.example.tinkoff.model.states.SenderType

data class MessageContent(
    val id: Int,
    val content: String,
    val senderName: String,
    val avatarUrl: String?,
    var reactions: List<Reaction>,
    val type: SenderType,
    val timestamp: Long
) :
    MessageContentInterface {
    fun toMessageContentRoom(streamName: String, topicName: String) = MessageContentRoom(
        id,
        streamName,
        topicName,
        content,
        senderName,
        avatarUrl,
        reactions,
        type.ordinal,
        timestamp
    )
}
