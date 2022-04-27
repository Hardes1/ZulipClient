package com.example.tinkoff.data.classes

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
) : StreamsInterface
