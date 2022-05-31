package com.example.tinkoff.presentation.classes

import com.example.tinkoff.model.room.entities.StreamHeaderRoom
import com.example.tinkoff.model.states.StreamType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StreamHeader(
    @SerialName("stream_id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @Transient
    var isSelected: Boolean = false
) :
    StreamsInterface {
    fun toStreamHeaderRoom(type: StreamType) =
        StreamHeaderRoom(
            id = id,
            name = name,
            type = type.ordinal
        )
}
