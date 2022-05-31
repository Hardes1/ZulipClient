package com.example.tinkoff.model.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tinkoff.model.states.SenderType
import com.example.tinkoff.presentation.classes.MessageContent
import com.example.tinkoff.presentation.classes.Reaction

@Entity(
    tableName = "MessageContent"
)
data class MessageContentRoom(
    @PrimaryKey @ColumnInfo(name = "message_id") val id: Int,
    @ColumnInfo(name = "stream_name") val streamName: String,
    @ColumnInfo(name = "topic_name") val topicName: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "sender_name") val senderName: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "reactions") val reactions: List<Reaction>,
    @ColumnInfo(name = "senderType") val senderType: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long

) {
    fun toMessageContent() = MessageContent(
        id,
        content,
        senderName,
        avatarUrl,
        reactions,
        SenderType.values().first { it.ordinal == senderType },
        timestamp
    )
}
