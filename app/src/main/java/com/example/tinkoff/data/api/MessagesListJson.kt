package com.example.tinkoff.data.api

import com.example.tinkoff.data.api.MessageJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessagesListJson(
    @SerialName("messages")
    val messages: List<MessageJson>,
)
