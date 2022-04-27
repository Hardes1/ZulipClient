package com.example.tinkoff.data.classes

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
    StreamsInterface
