package com.example.tinkoff.presentation.classes

import com.example.tinkoff.model.room.entities.TopicHeaderRoom
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TopicHeader(
    @SerialName("max_id")
    val id: Int,
    @Transient
    val streamId: Int = -1,
    @SerialName("name")
    val name: String
) : StreamsInterface {
    fun toTopicHeaderRoom() = TopicHeaderRoom(
        id = id,
        streamId = streamId,
        name = name
    )
}
