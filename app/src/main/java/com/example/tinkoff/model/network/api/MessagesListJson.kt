package com.example.tinkoff.model.network.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessagesListJson(
    @SerialName("messages")
    val messages: List<MessageJson>,
)
